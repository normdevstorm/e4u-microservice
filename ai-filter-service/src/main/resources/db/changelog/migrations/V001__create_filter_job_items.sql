-- =============================================================================
-- V001: Create filter_job_items table in e4u_ai_filter
-- =============================================================================
-- NOTE: Spring Batch metadata tables (BATCH_JOB_INSTANCE, BATCH_JOB_EXECUTION,
--       BATCH_STEP_EXECUTION, etc.) are auto-created by Spring Batch via
--       spring.batch.jdbc.initialize-schema=always — not managed by Liquibase.
--
-- This migration only creates application-specific custom tables.
-- =============================================================================

CREATE TABLE IF NOT EXISTS filter_job_items
(
    id               UUID         NOT NULL DEFAULT gen_random_uuid(),
    job_execution_id BIGINT       NOT NULL,   -- References BATCH_JOB_EXECUTION.JOB_EXECUTION_ID
    word_id          UUID         NOT NULL,   -- References global_dictionary.id in e4u_learning (cross-DB)
    word_lemma       VARCHAR(100) NOT NULL,
    filter_result    VARCHAR(20)  NOT NULL,   -- SAFE | FLAGGED | NEEDS_REVIEW
    ai_reason        TEXT,
    confidence_score FLOAT,
    processed_at     TIMESTAMP    NOT NULL DEFAULT now(),

    CONSTRAINT pk_filter_job_items PRIMARY KEY (id)
);

-- Index for querying all results of a single batch run
CREATE INDEX IF NOT EXISTS idx_fji_job_execution_id ON filter_job_items (job_execution_id);

-- Index for looking up AI filter history of a specific word
CREATE INDEX IF NOT EXISTS idx_fji_word_id ON filter_job_items (word_id);

-- Index for filtering by result type (e.g. fetch all FLAGGED words)
CREATE INDEX IF NOT EXISTS idx_fji_filter_result ON filter_job_items (filter_result);
