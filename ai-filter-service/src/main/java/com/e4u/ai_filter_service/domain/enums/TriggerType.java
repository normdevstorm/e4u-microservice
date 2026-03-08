package com.e4u.ai_filter_service.domain.enums;

/**
 * Describes what initiated a batch job execution.
 */
public enum TriggerType {
    /** Triggered automatically by the {@code @Scheduled} cron expression. */
    SCHEDULED,

    /**
     * Triggered manually via the REST API ({@code POST /api/filter-jobs/trigger}).
     */
    MANUAL
}
