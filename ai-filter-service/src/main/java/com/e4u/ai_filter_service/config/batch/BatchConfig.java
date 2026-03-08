package com.e4u.ai_filter_service.config.batch;

import com.e4u.ai_filter_service.common.constants.FilterConstants;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.batch.BatchDataSourceScriptDatabaseInitializer;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.sql.init.DatabaseInitializationMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import javax.sql.DataSource;

/**
 * Spring Batch infrastructure configuration.
 *
 * <p>
 * {@code @EnableBatchProcessing} is explicitly pointed to the {@code @Primary}
 * datasource ({@code aiFilterDataSource}) and its transaction manager
 * ({@code aiFilterTransactionManager}). This ensures all Spring Batch metadata
 * tables (BATCH_JOB_INSTANCE, BATCH_JOB_EXECUTION, etc.) are created and
 * managed in {@code e4u_ai_filter}, not {@code e4u_learning}.
 *
 * <p>
 * <strong>Schema initialisation note</strong>: In Spring Boot 3.x,
 * {@code @EnableBatchProcessing} causes Spring Boot's
 * {@code BatchAutoConfiguration}
 * to back off entirely (via {@code @ConditionalOnMissingBean(annotation =
 * EnableBatchProcessing.class)}). This means
 * {@code spring.batch.jdbc.initialize-schema}
 * in the YAML is ignored. We therefore register
 * {@link BatchDataSourceScriptDatabaseInitializer} explicitly so the BATCH_*
 * metadata tables are always created in {@code e4u_ai_filter} on startup.
 */
@Configuration
@EnableBatchProcessing(dataSourceRef = FilterConstants.AI_FILTER_DS, transactionManagerRef = FilterConstants.AI_FILTER_TM)
public class BatchConfig {

    /**
     * Manually registers the Spring Batch schema initializer because
     * {@code BatchAutoConfiguration} (which normally creates it) is disabled when
     * {@code @EnableBatchProcessing} is present.
     *
     * <p>
     * {@link DatabaseInitializationMode#ALWAYS} ensures the BATCH_* DDL is applied
     * on every startup; Spring Batch's scripts are idempotent (CREATE TABLE IF NOT
     * EXISTS) so this is safe.
     */
    @Bean
    public BatchDataSourceScriptDatabaseInitializer batchDataSourceScriptDatabaseInitializer(
            @Qualifier(FilterConstants.AI_FILTER_DS) DataSource dataSource) {
        BatchProperties props = new BatchProperties();
        props.getJdbc().setInitializeSchema(DatabaseInitializationMode.ALWAYS);
        return new BatchDataSourceScriptDatabaseInitializer(dataSource, props.getJdbc());
    }

    /**
     * Task executor for async job launching via REST API.
     * The @Scheduled trigger uses synchronous launch (blocking is fine for nightly
     * batch). The manual REST trigger uses this executor to avoid blocking the HTTP
     * thread.
     */
    @Bean(name = "batchTaskExecutor")
    public TaskExecutor batchTaskExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("batch-");
        executor.setConcurrencyLimit(1); // Only one job run at a time
        return executor;
    }
}
