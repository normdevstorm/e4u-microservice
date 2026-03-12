--liquibase formatted sql
--changeset dev:014-stats-snapshots comment:Create user_stats_snapshots table for admin historical stats

-- ============================================
-- Table: user_stats_snapshots
-- Daily point-in-time snapshot of each user's aggregated stats.
-- Written once per (user_id, snapshot_date) by the midnight scheduler.
-- Immutable after creation — never updated, only inserted.
-- Used by admin endpoints for progress trajectory and historical reports.
-- ============================================
CREATE TABLE IF NOT EXISTS public.user_stats_snapshots (
    id                        UUID         NOT NULL PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id                   UUID         NOT NULL,

    -- Calendar date of this snapshot (UTC). Unique per user per day.
    snapshot_date             DATE         NOT NULL,

    current_streak            INT          NOT NULL DEFAULT 0,
    longest_streak            INT          NOT NULL DEFAULT 0,
    total_words_learned       INT          NOT NULL DEFAULT 0,
    total_study_time_minutes  BIGINT       NOT NULL DEFAULT 0,

    -- Stored as 0.0–1.0
    overall_accuracy          DOUBLE PRECISION NOT NULL DEFAULT 0.0,

    -- Total COMPLETED sessions at the time of this snapshot
    sessions_completed_total  INT          NOT NULL DEFAULT 0,

    -- BaseEntity audit fields
    created_at                TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at                TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    deleted                   BOOLEAN      NOT NULL DEFAULT FALSE,
    deleted_at                TIMESTAMPTZ,
    created_by                TEXT,
    updated_by                TEXT,

    CONSTRAINT uq_snapshot_user_date UNIQUE (user_id, snapshot_date)
);

-- Primary access pattern: get all snapshots for a user in a date range
CREATE INDEX IF NOT EXISTS idx_snapshot_user_date ON public.user_stats_snapshots (user_id, snapshot_date);

-- Cross-user query on a specific date (admin: leaderboard as-of a past date)
CREATE INDEX IF NOT EXISTS idx_snapshot_date ON public.user_stats_snapshots (snapshot_date);

--rollback DROP INDEX IF EXISTS idx_snapshot_date;
--rollback DROP INDEX IF EXISTS idx_snapshot_user_date;
--rollback DROP TABLE IF EXISTS public.user_stats_snapshots;
