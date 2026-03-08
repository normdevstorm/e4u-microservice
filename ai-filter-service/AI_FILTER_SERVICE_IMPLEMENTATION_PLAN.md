# AI Filter Service — Implementation Plan

> **Purpose:** Scheduled batch processing + AI-powered word filtration.  
> **Reads from:** `e4u_learning` DB (shared with `learning-service`) — read-only.  
> **Writes to:** `e4u_ai_filter` DB (own) — Spring Batch metadata + custom job results.  
> **Framework:** Spring Boot 3.x + Spring Batch 5.x

---

## Table of Contents

1. [Architecture Overview](#1-architecture-overview)
2. [Package Structure](#2-package-structure)
3. [Step 1 — build.gradle](#3-step-1--buildgradle)
4. [Step 2 — Dual DataSource Configuration](#4-step-2--dual-datasource-configuration)
5. [Step 3 — Domain Layer (e4u_ai_filter DB)](#5-step-3--domain-layer-e4u_ai_filter-db)
6. [Step 4 — Read-Only Learning Domain Stubs](#6-step-4--read-only-learning-domain-stubs)
7. [Step 5 — Liquibase Migration](#7-step-5--liquibase-migration)
8. [Step 6 — Spring Batch Configuration](#8-step-6--spring-batch-configuration)
9. [Step 7 — Batch Pipeline (Reader / Processor / Writer)](#9-step-7--batch-pipeline-reader--processor--writer)
10. [Step 8 — AI API Client](#10-step-8--ai-api-client)
11. [Step 9 — Scheduler](#11-step-9--scheduler)
12. [Step 10 — REST API (Job Management)](#12-step-10--rest-api-job-management)
13. [Step 11 — Config Server YAML](#13-step-11--config-server-yaml)
14. [Step 12 — Docker Setup](#14-step-12--docker-setup)
15. [Step 13 — Main Application Class](#15-step-13--main-application-class)
16. [DataSource Wiring Summary](#16-datasource-wiring-summary)
17. [Implementation Checklist](#17-implementation-checklist)

---

## 1. Architecture Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                      ai-filter-service                           │
│                                                                  │
│  ┌─────────────┐    ┌──────────────────────────────────────┐    │
│  │  Scheduler  │───▶│          Spring Batch Job            │    │
│  │ (2AM daily) │    │  ┌──────────┐  ┌───────────────────┐ │    │
│  └─────────────┘    │  │  Step 1  │  │     Step 2        │ │    │
│                     │  │ (filter) │  │ (write-back flags)│ │    │
│  ┌─────────────┐    │  └──────────┘  └───────────────────┘ │    │
│  │ REST API    │───▶│  JobLauncher / JobOperator            │    │
│  │ (manual     │    └──────────────────────────────────────┘    │
│  │  trigger)   │                   │                            │
│  └─────────────┘                   │                            │
│                                    ▼                            │
│  ┌──────────────────┐   ┌──────────────────────────────────┐   │
│  │  e4u_learning DB │   │       e4u_ai_filter DB           │   │
│  │  (READ-ONLY)     │   │  Spring Batch metadata tables    │   │
│  │                  │   │  + filter_job_items (custom)     │   │
│  │  global_dictionary│  └──────────────────────────────────┘   │
│  │  word_context_    │                                          │
│  │  template        │   ┌──────────────────────────────────┐   │
│  └──────────────────┘   │       AI API (External)          │   │
│                         │  (OpenAI / custom endpoint)      │   │
│                         └──────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. Package Structure

```
src/main/java/com/e4u/ai_filter_service/
│
├── AiFilterServiceApplication.java          ← @SpringBootApplication (no @EnableBatchProcessing here)
│
├── config/
│   ├── datasource/
│   │   ├── AiFilterDataSourceConfig.java    ← @Primary DS → e4u_ai_filter
│   │   └── LearningDataSourceConfig.java    ← Secondary DS → e4u_learning (read-only)
│   ├── batch/
│   │   └── BatchConfig.java                 ← @EnableBatchProcessing wired to @Primary DS
│   ├── AiClientConfig.java                  ← RestClient / WebClient bean for AI calls
│   └── AppConfig.java                       ← General beans (ObjectMapper, etc.)
│
├── domain/                                  ← Entities for e4u_ai_filter DB only
│   ├── entity/
│   │   └── FilterJobItem.java               ← Custom per-word AI result record
│   └── enums/
│       ├── WordFilterResult.java            ← SAFE | FLAGGED | NEEDS_REVIEW
│       └── TriggerType.java                 ← SCHEDULED | MANUAL
│
├── learning/                                ← Read-only stubs from e4u_learning DB
│   ├── entity/
│   │   └── GlobalDictionaryReadOnly.java    ← Lightweight projection entity
│   └── repository/
│       └── GlobalDictionaryReadRepository.java  ← JPA repo on learningEntityManager
│
├── repository/
│   └── FilterJobItemRepository.java         ← JPA repo on aiFilterEntityManager
│
├── batch/
│   ├── job/
│   │   └── WordFilterJobConfig.java         ← Job + Step bean definitions
│   ├── reader/
│   │   └── GlobalDictionaryItemReader.java  ← JdbcPagingItemReader on e4u_learning
│   ├── processor/
│   │   └── WordFilterItemProcessor.java     ← Calls AiApiClient, maps to FilterJobItem
│   └── writer/
│       └── FilterJobItemWriter.java         ← JdbcBatchItemWriter to filter_job_items
│
├── scheduler/
│   └── FilterJobScheduler.java              ← @Scheduled → JobLauncher.run()
│
├── client/
│   ├── AiApiClient.java                     ← Interface / abstract contract
│   ├── OpenAiApiClient.java                 ← OpenAI implementation
│   └── dto/
│       ├── AiFilterRequest.java             ← DTO sent to AI API
│       └── AiFilterResponse.java            ← DTO received from AI API
│
├── controller/
│   └── FilterJobController.java             ← REST endpoints for manual trigger + status
│
├── service/
│   └── FilterJobManagementService.java      ← Manual trigger, status query, re-run
│
└── common/
    ├── constants/
    │   └── FilterConstants.java             ← chunk size, cron, qualifier names
    ├── properties/
    │   ├── BatchProperties.java             ← @ConfigurationProperties("batch")
    │   └── AiApiProperties.java             ← @ConfigurationProperties("ai.api")
    └── exception/
        └── AiApiException.java              ← Wraps AI call failures

src/main/resources/
├── application.yml                          ← Bootstrap → Config Server import
└── db/
    └── changelog/
        ├── db.changelog-master.yaml         ← Liquibase master
        └── migrations/
            └── V001__create_filter_job_items.sql
```

---

## 3. Step 1 — `build.gradle`

### Changes from current state

| Current (broken)                                      | Fix                                                      |
|-------------------------------------------------------|----------------------------------------------------------|
| `spring-boot-starter-4.0.3` (does not exist)          | Downgrade to `3.3.6` (matches learning-service)          |
| `spring-boot-starter-actuator-test` (invalid)         | Replace all invalid test starters with `spring-boot-starter-test` |
| `spring-boot-starter-data-jpa-test` (invalid)         | ↑ same                                                   |
| `spring-boot-starter-data-redis-test` (invalid)       | ↑ same                                                   |
| `spring-boot-starter-webmvc-test` (invalid)           | ↑ same                                                   |
| Missing `spring-cloud-starter-config`                 | Add (to pull config from configservice)                  |
| Missing `spring-boot-starter-batch`                   | Add                                                      |
| Missing `liquibase-core`                              | Add (DB migrations for e4u_ai_filter)                    |
| Missing `springdoc-openapi-starter-webmvc-ui`         | Add (Swagger UI)                                         |
| Missing `mapstruct`                                   | Add                                                      |
| Missing `net.logstash.logback:logstash-logback-encoder` | Add (structured logging)                              |
| Missing Spring Cloud BOM                             | Add `ext.springCloudVersion` + `dependencyManagement`    |

### Final `build.gradle`

```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.3.6'
    id 'io.spring.dependency-management' version '1.1.7'
}

group = 'com.e4u'
version = '0.0.1-SNAPSHOT'
description = 'AI Service for word filtration'

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

ext {
    set('springCloudVersion', "2023.0.4")
}

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom "org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}"
    }
}

dependencies {
    // Spring Cloud Config Client
    implementation 'org.springframework.cloud:spring-cloud-starter-config'

    // Spring Boot Core
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'org.springframework.boot:spring-boot-starter-validation'

    // Spring Batch (includes spring-batch-core + spring-batch-infrastructure)
    implementation 'org.springframework.boot:spring-boot-starter-batch'

    // Database
    implementation 'org.liquibase:liquibase-core'
    runtimeOnly 'org.postgresql:postgresql'

    // API Docs
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0'

    // Utilities
    implementation 'org.mapstruct:mapstruct:1.6.3'
    implementation 'net.logstash.logback:logstash-logback-encoder:8.0'

    // Lombok
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    annotationProcessor 'org.mapstruct:mapstruct-processor:1.6.3'
    annotationProcessor 'org.projectlombok:lombok-mapstruct-binding:0.2.0'

    // Dev
    developmentOnly 'org.springframework.boot:spring-boot-devtools'

    // Test
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.batch:spring-batch-test'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

---

## 4. Step 2 — Dual DataSource Configuration

### Critical constraint
Spring Batch 5 with Spring Boot 3.x **auto-configures** `JobRepository` using the `@Primary` `DataSource`.  
With two datasources, you must:
1. Mark one as `@Primary` (the service's own `e4u_ai_filter`)
2. Disable Spring Boot's autoconfiguration of the secondary datasource
3. Explicitly pass the primary DS + TM to `@EnableBatchProcessing`

### `AiFilterDataSourceConfig.java` — `@Primary` → `e4u_ai_filter`

```java
@Configuration
@EnableTransactionManagement
public class AiFilterDataSourceConfig {

    // Reads from: spring.datasource.* (own DB)
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties aiFilterDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource aiFilterDataSource(
            @Qualifier("aiFilterDataSourceProperties") DataSourceProperties props) {
        return props.initializeDataSourceBuilder().build();
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean aiFilterEntityManager(
            @Qualifier("aiFilterDataSource") DataSource dataSource,
            JpaProperties jpaProperties) {
        // entity scan: com.e4u.ai_filter_service.domain.entity
    }

    @Bean
    @Primary
    public PlatformTransactionManager aiFilterTransactionManager(
            @Qualifier("aiFilterEntityManager") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
```

### `LearningDataSourceConfig.java` — Secondary → `e4u_learning`

```java
@Configuration
public class LearningDataSourceConfig {

    // Reads from: spring.learning-datasource.* (shared learning DB)
    @Bean
    @ConfigurationProperties("spring.learning-datasource")
    public DataSourceProperties learningDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Qualifier("learningDataSource")
    public DataSource learningDataSource(
            @Qualifier("learningDataSourceProperties") DataSourceProperties props) {
        return props.initializeDataSourceBuilder().build();
    }

    @Bean
    @Qualifier("learningEntityManager")
    public LocalContainerEntityManagerFactoryBean learningEntityManager(
            @Qualifier("learningDataSource") DataSource dataSource,
            JpaProperties jpaProperties) {
        // entity scan: com.e4u.ai_filter_service.learning.entity
        // ddl-auto: none (never touch learning DB schema)
    }

    @Bean
    @Qualifier("learningTransactionManager")
    public PlatformTransactionManager learningTransactionManager(
            @Qualifier("learningEntityManager") EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
```

### `BatchConfig.java`

```java
@Configuration
@EnableBatchProcessing(
    dataSourceRef = "aiFilterDataSource",
    transactionManagerRef = "aiFilterTransactionManager"
)
public class BatchConfig {
    // Spring Boot autoconfigures JobRepository + JobLauncher when DS is pointed correctly
    // Custom thread pool executor for async job launching can be configured here
}
```

---

## 5. Step 3 — Domain Layer (`e4u_ai_filter` DB)

### `FilterJobItem.java`

Stores the per-word AI filtration result for a given batch job execution.

```java
@Entity
@Table(name = "filter_job_items")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class FilterJobItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "job_execution_id", nullable = false)
    private Long jobExecutionId;          // FK to BATCH_JOB_EXECUTION

    @Column(name = "word_id", nullable = false)
    private UUID wordId;                  // ref to global_dictionary.id in e4u_learning

    @Column(name = "word_lemma", nullable = false, length = 100)
    private String wordLemma;

    @Enumerated(EnumType.STRING)
    @Column(name = "filter_result", nullable = false, length = 20)
    private WordFilterResult filterResult; // SAFE | FLAGGED | NEEDS_REVIEW

    @Column(name = "ai_reason", columnDefinition = "TEXT")
    private String aiReason;              // Short explanation from AI

    @Column(name = "confidence_score")
    private Float confidenceScore;        // 0.0 – 1.0 from AI model

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;
}
```

### `WordFilterResult.java`

```java
public enum WordFilterResult {
    SAFE,          // Word is clean, no action needed
    FLAGGED,       // Word is inappropriate, should be hidden/removed
    NEEDS_REVIEW   // Ambiguous — requires human review
}
```

### `TriggerType.java`

```java
public enum TriggerType {
    SCHEDULED,  // Triggered by @Scheduled cron
    MANUAL      // Triggered via REST API
}
```

---

## 6. Step 4 — Read-Only Learning Domain Stubs

These are **lightweight projections** — only the fields needed for filtration.  
**No cascade, no DDL, no write operations**.

### `GlobalDictionaryReadOnly.java`

```java
@Entity
@Table(name = "global_dictionary")
@Getter @Setter @NoArgsConstructor
// Uses learningEntityManager — mapped to e4u_learning.global_dictionary
public class GlobalDictionaryReadOnly {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "lemma")
    private String lemma;

    @Column(name = "part_of_speech")
    private String partOfSpeech;

    @Column(name = "definition", columnDefinition = "TEXT")
    private String definition;

    @Column(name = "example_sentence", columnDefinition = "TEXT")
    private String exampleSentence;

    // NOTE: is_filtered_at added by migration in e4u_learning
    // Tracks when this word was last run through AI filter
    @Column(name = "ai_filtered_at")
    private Instant aiFilteredAt;

    @Column(name = "ai_filter_result", length = 20)
    private String aiFilterResult;        // Mirrors WordFilterResult enum value
}
```

> **Note:** The `ai_filtered_at` and `ai_filter_result` columns need to be added to `global_dictionary`  
> via a migration **in `learning-service`'s Liquibase** (or as a standalone script), since this service  
> should not own learning DB schema changes.

### `GlobalDictionaryReadRepository.java`

```java
// Annotated with @Repository — uses @Qualifier("learningEntityManager")
public interface GlobalDictionaryReadRepository
        extends JpaRepository<GlobalDictionaryReadOnly, UUID> {

    // Find words not yet processed (ai_filtered_at IS NULL)
    Page<GlobalDictionaryReadOnly> findByAiFilteredAtIsNull(Pageable pageable);

    // Find words to re-process (older than X days)
    @Query("SELECT g FROM GlobalDictionaryReadOnly g WHERE g.aiFilteredAt < :cutoff")
    Page<GlobalDictionaryReadOnly> findStaleWords(@Param("cutoff") Instant cutoff, Pageable pageable);
}
```

---

## 7. Step 5 — Liquibase Migration

### `db/changelog/db.changelog-master.yaml`

```yaml
databaseChangeLog:
  - include:
      file: db/changelog/migrations/V001__create_filter_job_items.sql
      relativeToChangelogFile: false
```

### `db/changelog/migrations/V001__create_filter_job_items.sql`

```sql
-- Spring Batch tables are auto-created by Spring Batch itself (spring.batch.jdbc.initialize-schema=always)
-- This migration only creates custom application tables

CREATE TABLE IF NOT EXISTS filter_job_items (
    id               UUID         NOT NULL DEFAULT gen_random_uuid(),
    job_execution_id BIGINT       NOT NULL,  -- references BATCH_JOB_EXECUTION
    word_id          UUID         NOT NULL,  -- references global_dictionary in e4u_learning
    word_lemma       VARCHAR(100) NOT NULL,
    filter_result    VARCHAR(20)  NOT NULL,  -- SAFE | FLAGGED | NEEDS_REVIEW
    ai_reason        TEXT,
    confidence_score FLOAT,
    processed_at     TIMESTAMP    NOT NULL DEFAULT now(),

    CONSTRAINT pk_filter_job_items PRIMARY KEY (id)
);

CREATE INDEX idx_fji_job_execution_id ON filter_job_items(job_execution_id);
CREATE INDEX idx_fji_word_id          ON filter_job_items(word_id);
CREATE INDEX idx_fji_filter_result    ON filter_job_items(filter_result);
```

---

## 8. Step 6 — Spring Batch Configuration

### `WordFilterJobConfig.java`

```java
@Configuration
@RequiredArgsConstructor
public class WordFilterJobConfig {

    private final JobRepository jobRepository;                  // auto-wired by Spring Batch
    private final PlatformTransactionManager aiFilterTxManager; // @Primary TM

    // ─────────────────────────────────────────
    // JOB
    // ─────────────────────────────────────────
    @Bean
    public Job wordFilterJob(Step wordFilterStep) {
        return new JobBuilder("wordFilterJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(wordFilterStep)
                .build();
    }

    // ─────────────────────────────────────────
    // STEP  (chunk-oriented)
    // ─────────────────────────────────────────
    @Bean
    public Step wordFilterStep(
            GlobalDictionaryItemReader reader,
            WordFilterItemProcessor processor,
            FilterJobItemWriter writer) {
        return new StepBuilder("wordFilterStep", jobRepository)
                .<GlobalDictionaryReadOnly, FilterJobItem>chunk(
                        filterConstants.getChunkSize(), aiFilterTxManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                    .retryLimit(3).retry(AiApiException.class)  // Retry on AI failures
                    .skipLimit(10).skip(Exception.class)        // Skip unrecoverable words
                .build();
    }
}
```

---

## 9. Step 7 — Batch Pipeline (Reader / Processor / Writer)

### `GlobalDictionaryItemReader.java`

```java
@Component
@StepScope
public class GlobalDictionaryItemReader implements ItemReader<GlobalDictionaryReadOnly> {

    // Uses JdbcPagingItemReader on learningDataSource
    // SELECT id, lemma, part_of_speech, definition, example_sentence
    // FROM global_dictionary
    // WHERE ai_filtered_at IS NULL
    // ORDER BY created_at ASC
    // → Chunk by FilterConstants.CHUNK_SIZE (e.g. 50)

    // NOTE: @StepScope ensures a fresh reader per job execution
    // Uses learningDataSource (read-only) — never aiFilterDataSource
}
```

### `WordFilterItemProcessor.java`

```java
@Component
@StepScope
public class WordFilterItemProcessor
        implements ItemProcessor<GlobalDictionaryReadOnly, FilterJobItem> {

    // 1. Build AiFilterRequest(lemma, definition, exampleSentence)
    // 2. Call AiApiClient.filter(request) → AiFilterResponse
    // 3. Map response to FilterJobItem
    // 4. Inject jobExecutionId from StepExecution context (@Value("#{stepExecution}"))
    // Throws AiApiException on HTTP 5xx (will be retried by Step config)
}
```

### `FilterJobItemWriter.java`

```java
@Component
@StepScope
public class FilterJobItemWriter implements ItemWriter<FilterJobItem> {

    // Batch INSERT into filter_job_items using aiFilterEntityManager
    // Optional write-back: UPDATE global_dictionary SET ai_filter_result, ai_filtered_at
    //   on learningDataSource (separate transaction, best-effort)
}
```

---

## 10. Step 8 — AI API Client

### `AiApiClient.java` (interface)

```java
public interface AiApiClient {
    AiFilterResponse filter(AiFilterRequest request);
    List<AiFilterResponse> filterBatch(List<AiFilterRequest> requests);
}
```

### `OpenAiApiClient.java` (implementation)

```java
@Component
public class OpenAiApiClient implements AiApiClient {

    // Uses RestClient (Spring 6) or WebClient
    // POST to ${ai.api.url}/v1/chat/completions
    // Authorization: Bearer ${ai.api.key}
    // Model: ${ai.api.model}

    // Prompt template:
    //   "Given the English word '{lemma}' (part of speech: {pos}),
    //    definition: '{definition}', example: '{example}',
    //    determine if this word is appropriate for a language-learning app
    //    for general audiences. Respond with JSON:
    //    { result: 'SAFE'|'FLAGGED'|'NEEDS_REVIEW', reason: '...', confidence: 0.0-1.0 }"

    // TODO: implement actual API call
}
```

### DTOs

```java
// AiFilterRequest.java
public record AiFilterRequest(
    UUID wordId,
    String lemma,
    String partOfSpeech,
    String definition,
    String exampleSentence
) {}

// AiFilterResponse.java
public record AiFilterResponse(
    UUID wordId,
    WordFilterResult result,  // SAFE | FLAGGED | NEEDS_REVIEW
    String reason,
    Float confidence
) {}
```

---

## 11. Step 9 — Scheduler

### `FilterJobScheduler.java`

```java
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "batch.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class FilterJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job wordFilterJob;

    @Scheduled(cron = "${batch.scheduler.cron:0 0 2 * * ?}")
    public void scheduledRun() throws Exception {
        JobParameters params = new JobParametersBuilder()
                .addLong("triggeredAt", System.currentTimeMillis())
                .addString("triggerType", TriggerType.SCHEDULED.name())
                .toJobParameters();
        jobLauncher.run(wordFilterJob, params);
    }
}
```

---

## 12. Step 10 — REST API (Job Management)

### Endpoints

| Method | Path                              | Description                        |
|--------|-----------------------------------|------------------------------------|
| `POST` | `/api/filter-jobs/trigger`        | Manually trigger a new job run     |
| `GET`  | `/api/filter-jobs`                | List all job executions            |
| `GET`  | `/api/filter-jobs/{executionId}`  | Get details for one execution      |
| `GET`  | `/api/filter-jobs/{executionId}/items` | List per-word results         |
| `POST` | `/api/filter-jobs/{executionId}/stop` | Stop a running job             |

### `FilterJobController.java`

```java
@RestController
@RequestMapping("/api/filter-jobs")
@RequiredArgsConstructor
public class FilterJobController {

    private final FilterJobManagementService filterJobManagementService;

    @PostMapping("/trigger")
    public ResponseEntity<JobExecutionDto> triggerJob() {
        // TODO: call filterJobManagementService.triggerManual()
    }

    @GetMapping
    public ResponseEntity<Page<JobExecutionDto>> listJobs(Pageable pageable) {
        // TODO: call filterJobManagementService.listExecutions(pageable)
    }

    @GetMapping("/{executionId}")
    public ResponseEntity<JobExecutionDto> getJob(@PathVariable Long executionId) {
        // TODO: call filterJobManagementService.getExecution(executionId)
    }

    @GetMapping("/{executionId}/items")
    public ResponseEntity<Page<FilterJobItemDto>> getJobItems(
            @PathVariable Long executionId, Pageable pageable) {
        // TODO: call filterJobManagementService.getItems(executionId, pageable)
    }

    @PostMapping("/{executionId}/stop")
    public ResponseEntity<Void> stopJob(@PathVariable Long executionId) {
        // TODO: call filterJobManagementService.stopExecution(executionId)
    }
}
```

---

## 13. Step 11 — Config Server YAML

### New file: `configservice/src/main/resources/shared/ai-filter-service.yml`

```yaml
# ===========================================
# Common settings for all environments
# ===========================================
server:
  port: 8086

spring:
  jpa:
    hibernate:
      ddl-auto: none
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
  batch:
    job:
      enabled: false        # Disable auto-run on startup; scheduler controls this
    jdbc:
      initialize-schema: always  # Let Spring Batch create its metadata tables

# Batch job properties
batch:
  chunk-size: 50
  scheduler:
    cron: "0 0 2 * * ?"    # 2AM daily
    enabled: true

# AI API properties
ai:
  api:
    url: ${AI_API_URL:http://localhost:8090}
    key: ${AI_API_KEY:}
    model: ${AI_MODEL:gpt-4o-mini}
    timeout-seconds: 30
    batch-size: 20          # Words per AI request call

# Eureka
eureka:
  client:
    service-url:
      defaultZone: ${EUREKA_SERVER_URL:http://localhost:9999/eureka}
  instance:
    prefer-ip-address: true

# Actuator
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,batch
  endpoint:
    health:
      show-details: always

# OpenAPI / Swagger
springdoc:
  api-docs:
    path: /v3/api-docs
    enabled: true
  swagger-ui:
    path: /swagger-ui.html
    enabled: true

# Logging
logging:
  level:
    root: info
    org.springframework.batch: info
    com.e4u.ai_filter_service: info

---
# ===========================================
# Development profile
# ===========================================
spring:
  config:
    activate:
      on-profile: dev

  # Primary DataSource → e4u_ai_filter (own DB)
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/e4u_ai_filter
    username: ${DB_USERNAME:nam}
    password: ${DB_PASSWORD:nam123}

  # Secondary DataSource → e4u_learning (shared, read-only)
  learning-datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/e4u_learning
    username: ${DB_USERNAME:nam}
    password: ${DB_PASSWORD:nam123}

  liquibase:
    contexts: dev

logging:
  level:
    org.springframework.batch: debug
    com.e4u.ai_filter_service: debug

---
# ===========================================
# Docker profile
# ===========================================
spring:
  config:
    activate:
      on-profile: docker

  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://postgres:5432/e4u_ai_filter
    username: ${DB_USERNAME:nam}
    password: ${DB_PASSWORD:nam123}

  learning-datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://postgres:5432/e4u_learning
    username: ${DB_USERNAME:nam}
    password: ${DB_PASSWORD:nam123}

  liquibase:
    contexts: docker

eureka:
  client:
    service-url:
      defaultZone: http://discovery-service:9999/eureka

---
# ===========================================
# Production profile
# ===========================================
spring:
  config:
    activate:
      on-profile: prod

  datasource:
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT:5432}/e4u_ai_filter
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

  learning-datasource:
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT:5432}/e4u_learning
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

  jpa:
    show-sql: false

  liquibase:
    contexts: prod

logging:
  level:
    root: warn
    com.e4u.ai_filter_service: info
```

### `application.yml` (bootstrap — in ai-filter-service itself)

```yaml
spring:
  application:
    name: ai-filter-service
  config:
    import: configserver:${CONFIG_SERVER_URL:http://localhost:8888}
  cloud:
    config:
      fail-fast: true
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
```

---

## 14. Step 12 — Docker Setup

### Changes to `docker-compose.yml` and `docker-compose.dev.yml`

**1. Add `e4u_ai_filter` to postgres `POSTGRES_MULTIPLE_DATABASES`:**

```yaml
# In both docker-compose.yml and docker-compose.dev.yml
environment:
  POSTGRES_MULTIPLE_DATABASES: e4u_auth,e4u_account,e4u_item,e4u_statistic,e4u_payment,e4u_learning,e4u_ai_filter
```

**2. Add `ai-filter-service` block to `docker-compose.yml`:**

```yaml
ai-filter-service:
  build:
    context: .
    dockerfile: ai-filter-service/Dockerfile
  container_name: e4u-ai-filter-service
  ports:
    - "8086:8086"
  environment:
    - SPRING_PROFILES_ACTIVE=docker
    - CONFIG_SERVER_URL=http://config-service:8888
    - AI_API_KEY=${AI_API_KEY}
    - AI_API_URL=${AI_API_URL:http://localhost:8090}
  depends_on:
    config-service:
      condition: service_healthy
    discovery-service:
      condition: service_healthy
    postgres:
      condition: service_healthy
  networks:
    - e4u-network
```

### `ai-filter-service/Dockerfile`

```dockerfile
# Build stage
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .

COPY ai-filter-service ./ai-filter-service

RUN chmod +x ./gradlew && ./gradlew :ai-filter-service:bootJar -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl

COPY --from=build /app/ai-filter-service/build/libs/*.jar app.jar

EXPOSE 8086

ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 15. Step 13 — Main Application Class

### `AiFilterServiceApplication.java`

```java
@SpringBootApplication
@EnableScheduling                    // Enable @Scheduled in FilterJobScheduler
// NOTE: @EnableBatchProcessing is in BatchConfig.java — not here
public class AiFilterServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiFilterServiceApplication.class, args);
    }
}
```

---

## 16. DataSource Wiring Summary

```
Bean Name                      Qualifier               Scope        Points to
─────────────────────────────────────────────────────────────────────────────────
aiFilterDataSource             @Primary                @Primary     e4u_ai_filter
aiFilterEntityManager          @Primary                @Primary     domain.entity.*
aiFilterTransactionManager     @Primary                @Primary     aiFilterEntityManager
─────────────────────────────────────────────────────────────────────────────────
learningDataSource             @Qualifier("learning…") Secondary    e4u_learning
learningEntityManager          @Qualifier("learning…") Secondary    learning.entity.*
learningTransactionManager     @Qualifier("learning…") Secondary    learningEntityManager
─────────────────────────────────────────────────────────────────────────────────
JobRepository (Spring Batch)   auto-wires @Primary     auto         → aiFilterDataSource
JobLauncher   (Spring Batch)   auto-wires @Primary     auto         → aiFilterTransactionManager
─────────────────────────────────────────────────────────────────────────────────
```

---

## 17. Implementation Checklist

```
Phase 1 — Foundation
  [ ] 1. Fix build.gradle (Spring Boot 3.3.6, add batch, cloud config, liquibase, mapstruct)
  [ ] 2. Update application.yml → bootstrap for config server
  [ ] 3. Create ai-filter-service.yml in configservice/shared/
  [ ] 4. Add e4u_ai_filter to POSTGRES_MULTIPLE_DATABASES in both docker-compose files

Phase 2 — DataSource & Batch Config
  [ ] 5.  AiFilterDataSourceConfig.java (@Primary)
  [ ] 6.  LearningDataSourceConfig.java (@Qualifier)
  [ ] 7.  BatchConfig.java (@EnableBatchProcessing pointing to @Primary DS)
  [ ] 8.  AppConfig.java (ObjectMapper, RestClient beans)
  [ ] 9.  BatchProperties.java + AiApiProperties.java (@ConfigurationProperties)

Phase 3 — Domain & Schema
  [ ] 10. FilterJobItem.java entity
  [ ] 11. WordFilterResult.java + TriggerType.java enums
  [ ] 12. FilterJobItemRepository.java
  [ ] 13. GlobalDictionaryReadOnly.java (learning stub entity)
  [ ] 14. GlobalDictionaryReadRepository.java (uses learningEntityManager)
  [ ] 15. Liquibase: db.changelog-master.yaml + V001__create_filter_job_items.sql
  [ ] 16. FilterConstants.java

Phase 4 — Batch Pipeline
  [ ] 17. GlobalDictionaryItemReader.java (JdbcPagingItemReader on learningDataSource)
  [ ] 18. WordFilterItemProcessor.java (calls AiApiClient)
  [ ] 19. FilterJobItemWriter.java (writes to aiFilterDataSource)
  [ ] 20. WordFilterJobConfig.java (Job + Step wiring with fault tolerance)

Phase 5 — AI Client
  [ ] 21. AiFilterRequest.java + AiFilterResponse.java (record DTOs)
  [ ] 22. AiApiClient.java (interface)
  [ ] 23. OpenAiApiClient.java (implementation with RestClient)
  [ ] 24. AiApiException.java

Phase 6 — Scheduler & REST API
  [ ] 25. FilterJobScheduler.java (@Scheduled + conditional)
  [ ] 26. FilterJobManagementService.java (manual trigger, status, stop)
  [ ] 27. FilterJobController.java (REST endpoints)

Phase 7 — Infrastructure
  [ ] 28. ai-filter-service/Dockerfile
  [ ] 29. Add service block to docker-compose.yml
  [ ] 30. Update POSTGRES_MULTIPLE_DATABASES in both compose files

Phase 8 — Testing
  [ ] 31. Unit test: WordFilterItemProcessor (mock AiApiClient)
  [ ] 32. Unit test: OpenAiApiClient (mock RestClient)
  [ ] 33. Integration test: WordFilterJob with @SpringBatchTest + embedded H2
```

---

*Last updated: 2026-03-07*
