package com.e4u.ai_filter_service.common.constants;

/**
 * Central constants for the AI filter batch service.
 * All tunable values should be driven by @ConfigurationProperties — these are
 * fallback defaults.
 */
public final class FilterConstants {

    private FilterConstants() {
    }

    // ─── DataSource qualifier names ──────────────────────────────────────────
    public static final String AI_FILTER_DS = "aiFilterDataSource";
    public static final String AI_FILTER_EM = "aiFilterEntityManager";
    public static final String AI_FILTER_TM = "aiFilterTransactionManager";

    public static final String LEARNING_DS = "learningDataSource";
    public static final String LEARNING_EM = "learningEntityManager";
    public static final String LEARNING_TM = "learningTransactionManager";

    // ─── Entity package scans ────────────────────────────────────────────────
    public static final String AI_FILTER_ENTITY_PACKAGE = "com.e4u.ai_filter_service.domain.entity";
    public static final String LEARNING_ENTITY_PACKAGE = "com.e4u.ai_filter_service.learning.entity";

    // ─── Spring Batch job/step names ─────────────────────────────────────────
    public static final String WORD_FILTER_JOB = "wordFilterJob";
    public static final String WORD_FILTER_STEP = "wordFilterStep";

    // ─── Batch defaults (overridden by BatchProperties) ──────────────────────
    public static final int DEFAULT_CHUNK_SIZE = 50;
    public static final int DEFAULT_RETRY_LIMIT = 3;
    public static final int DEFAULT_SKIP_LIMIT = 10;
    public static final String DEFAULT_CRON = "0 0 2 * * ?";

    // ─── Job parameter keys ──────────────────────────────────────────────────
    public static final String PARAM_TRIGGERED_AT = "triggeredAt";
    public static final String PARAM_TRIGGER_TYPE = "triggerType";
}
