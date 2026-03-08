package com.e4u.ai_filter_service.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.e4u.ai_filter_service.common.constants.FilterConstants;

/**
 * Batch job configuration properties.
 * Mapped from {@code batch.*} in ai-filter-service.yml.
 * Registered as a bean via {@code @EnableConfigurationProperties} in AppConfig.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "batch")
public class BatchProperties {

    /** Number of words processed per chunk (read → process → write transaction). */
    private int chunkSize = FilterConstants.DEFAULT_CHUNK_SIZE;

    private Scheduler scheduler = new Scheduler();

    @Getter
    @Setter
    public static class Scheduler {
        /** Cron expression for the scheduled trigger. Default: 2AM daily. */
        private String cron = FilterConstants.DEFAULT_CRON;

        /**
         * Set to false to disable the @Scheduled trigger (e.g. in test environments).
         */
        private boolean enabled = true;
    }
}
