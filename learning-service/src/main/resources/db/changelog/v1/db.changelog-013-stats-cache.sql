--liquibase formatted sql
--changeset dev:013-stats-cache comment:Create user_stats_cache table for leaderboard feature

-- ============================================
-- Table: user_stats_cache
-- Pre-aggregated stats per user, updated asynchronously
-- after every session completion (event-driven + hourly fallback).
-- One row per user. PK = user_id (no UUID generation).
-- Used exclusively for leaderboard reads — never for the user's own stats screen.
-- ============================================
CREATE TABLE IF NOT EXISTS public.user_stats_cache (
    user_id                   UUID         NOT NULL PRIMARY KEY,
    username                  VARCHAR(255),

    current_streak            INT          NOT NULL DEFAULT 0,
    -- Stored incrementally: max(longest_streak, current_streak).
    -- Never decreases — avoids full history scan.
    longest_streak            INT          NOT NULL DEFAULT 0,

    total_words_learned       INT          NOT NULL DEFAULT 0,
    total_study_time_minutes  BIGINT       NOT NULL DEFAULT 0,

    -- Stored as 0.0–1.0 (normalised, not a percentage).
    overall_accuracy          DOUBLE PRECISION NOT NULL DEFAULT 0.0,

    -- Timestamp of the last successful sync; used by the hourly fallback scheduler.
    last_synced_at            TIMESTAMPTZ,

    -- BaseEntity audit fields
    created_at                TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at                TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    deleted                   BOOLEAN      NOT NULL DEFAULT FALSE,
    deleted_at                TIMESTAMPTZ,
    created_by                TEXT,
    updated_by                TEXT
);

-- Indexes to support ORDER BY on each leaderboard sort option
CREATE INDEX IF NOT EXISTS idx_cache_words    ON public.user_stats_cache (total_words_learned    DESC);
CREATE INDEX IF NOT EXISTS idx_cache_streak   ON public.user_stats_cache (current_streak         DESC);
CREATE INDEX IF NOT EXISTS idx_cache_accuracy ON public.user_stats_cache (overall_accuracy       DESC);
CREATE INDEX IF NOT EXISTS idx_cache_time     ON public.user_stats_cache (total_study_time_minutes DESC);

--rollback DROP INDEX IF EXISTS idx_cache_time;
--rollback DROP INDEX IF EXISTS idx_cache_accuracy;
--rollback DROP INDEX IF EXISTS idx_cache_streak;
--rollback DROP INDEX IF EXISTS idx_cache_words;
--rollback DROP TABLE IF EXISTS public.user_stats_cache;
