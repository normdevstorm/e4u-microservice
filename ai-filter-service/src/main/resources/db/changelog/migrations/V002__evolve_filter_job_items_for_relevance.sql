-- =============================================================================
-- V002: Evolve filter_job_items to support per-user relevance scoring
--       against word_context_templates as the batch source.
-- =============================================================================
-- Changes:
--   1. Add user_id          — the learner this score was computed for
--   2. Add word_context_template_id — FK reference to the source template row
--   3. Rename filter_result → relevance_tier  (SAFE/FLAGGED → HIGH/MEDIUM/LOW)
--   4. Rename confidence_score → relevance_score  (same semantics, new name)
--   5. Add covering indexes for per-user and per-template queries
-- =============================================================================

-- 1. Add user_id column (initially nullable for safe migration)
ALTER TABLE filter_job_items
    ADD COLUMN IF NOT EXISTS user_id UUID;

-- 2. Add word_context_template_id (nullable — legacy rows won't have one)
ALTER TABLE filter_job_items
    ADD COLUMN IF NOT EXISTS word_context_template_id UUID;

-- 3. Rename filter_result → relevance_tier
--    Guard: only rename if the old column exists and the new one doesn't
DO $$
BEGIN
    IF EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_name = 'filter_job_items' AND column_name = 'filter_result'
        )
       AND NOT EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_name = 'filter_job_items' AND column_name = 'relevance_tier'
        ) THEN
        ALTER TABLE filter_job_items RENAME COLUMN filter_result TO relevance_tier;
    END IF;
END $$;

-- 4. Rename confidence_score → relevance_score
DO $$
BEGIN
    IF EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_name = 'filter_job_items' AND column_name = 'confidence_score'
        )
       AND NOT EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_name = 'filter_job_items' AND column_name = 'relevance_score'
        ) THEN
        ALTER TABLE filter_job_items RENAME COLUMN confidence_score TO relevance_score;
    END IF;
END $$;

-- 5. Back-fill user_id for any pre-existing rows (sentinel UUID for legacy rows)
UPDATE filter_job_items
   SET user_id = '00000000-0000-0000-0000-000000000000'
 WHERE user_id IS NULL;

-- 6. Enforce NOT NULL on user_id now that legacy rows are patched
ALTER TABLE filter_job_items
    ALTER COLUMN user_id SET NOT NULL;

-- 7. Indexes for per-user and per-template access patterns
CREATE INDEX IF NOT EXISTS idx_fji_user_id
    ON filter_job_items (user_id);

CREATE INDEX IF NOT EXISTS idx_fji_user_word
    ON filter_job_items (user_id, word_id);

CREATE INDEX IF NOT EXISTS idx_fji_relevance_tier
    ON filter_job_items (relevance_tier);

CREATE INDEX IF NOT EXISTS idx_fji_word_context_template
    ON filter_job_items (word_context_template_id);

-- 8. Drop obsolete index (renamed column, so old index is invalid)
DROP INDEX IF EXISTS idx_fji_filter_result;

-- =============================================================================
-- Result: filter_job_items now has columns:
--   id, job_execution_id, user_id (NOT NULL), word_id, word_lemma,
--   relevance_tier, relevance_score, ai_reason, processed_at,
--   word_context_template_id
-- =============================================================================
