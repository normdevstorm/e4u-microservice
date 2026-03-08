package com.e4u.ai_filter_service.batch.job;

import com.e4u.ai_filter_service.batch.listener.BatchItemLoggingListener;
import com.e4u.ai_filter_service.batch.processor.WordFilterItemProcessor;
import com.e4u.ai_filter_service.batch.reader.GlobalDictionaryItemReader;
import com.e4u.ai_filter_service.batch.writer.FilterJobItemWriter;
import com.e4u.ai_filter_service.batch.domain.UserWordPair;
import com.e4u.ai_filter_service.common.constants.FilterConstants;
import com.e4u.ai_filter_service.common.exception.AiApiException;
import com.e4u.ai_filter_service.common.properties.BatchProperties;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.ItemWriteListener;
import com.e4u.ai_filter_service.domain.entity.FilterJobItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Spring Batch Job and Step bean definitions for the AI word filtration
 * pipeline.
 *
 * <p>
 * Pipeline: {@code GlobalDictionaryItemReader} →
 * {@code WordFilterItemProcessor} → {@code FilterJobItemWriter}
 *
 * <p>
 * Fault tolerance:
 * <ul>
 * <li>Retry: up to 3 times on {@link AiApiException} (transient AI API
 * errors)</li>
 * <li>Skip: up to 10 items on any non-retryable exception (malformed data,
 * etc.)</li>
 * </ul>
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WordFilterJobConfig {

    /**
     * Auto-configured by Spring Batch, wired to aiFilterDataSource
     * via @EnableBatchProcessing.
     */
    private final JobRepository jobRepository;

    @Qualifier(FilterConstants.AI_FILTER_TM)
    private final PlatformTransactionManager aiFilterTransactionManager;

    private final BatchProperties batchProperties;

    // ─── Job ─────────────────────────────────────────────────────────────────

    @Bean(FilterConstants.WORD_FILTER_JOB)
    public Job wordFilterJob(Step wordFilterStep) {
        return new JobBuilder(FilterConstants.WORD_FILTER_JOB, jobRepository)
                // RunIdIncrementer ensures each scheduled/manual trigger creates a new
                // JobInstance
                .incrementer(new RunIdIncrementer())
                .start(wordFilterStep)
                .build();
    }

    // ─── Step ─────────────────────────────────────────────────────────────────

    @Bean(FilterConstants.WORD_FILTER_STEP)
    public Step wordFilterStep(
            GlobalDictionaryItemReader reader,
            WordFilterItemProcessor processor,
            FilterJobItemWriter writer,
            BatchItemLoggingListener loggingListener) {

        int chunkSize = batchProperties.getChunkSize();
        log.info("Configuring wordFilterStep with chunkSize={}", chunkSize);

        return new StepBuilder(FilterConstants.WORD_FILTER_STEP, jobRepository)
                .<UserWordPair, FilterJobItem>chunk(chunkSize, aiFilterTransactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener((ItemReadListener<UserWordPair>) loggingListener)
                .listener((ItemWriteListener<FilterJobItem>) loggingListener)
                // ── Fault tolerance ──────────────────────────────────────────
                .faultTolerant()
                // Retry up to 3x on transient AI API failures
                .retryLimit(FilterConstants.DEFAULT_RETRY_LIMIT)
                .retry(AiApiException.class)
                // Skip up to 10 unrecoverable items (e.g. null lemma, bad data)
                .skipLimit(FilterConstants.DEFAULT_SKIP_LIMIT)
                .skip(Exception.class)
                .noSkip(AiApiException.class) // Don't skip AI errors — retry them instead
                .build();
    }
}
