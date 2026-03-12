--liquibase formatted sql
--changeset dev:010-user-profiles comment:Create user_profiles table for onboarding module

CREATE TABLE IF NOT EXISTS public.user_profiles (
    profile_id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id                 UUID         NOT NULL UNIQUE,
    occupation              TEXT,
    interests               TEXT[],
    proficiency_baseline    TEXT,
    current_proficiency     TEXT,
    daily_time_commitment   INT          NOT NULL DEFAULT 15,
    privacy_consent         BOOLEAN      NOT NULL DEFAULT FALSE,
    is_onboarding_complete  BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at              TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    deleted                 BOOLEAN      NOT NULL DEFAULT FALSE,
    deleted_at              TIMESTAMPTZ,
    created_by              TEXT,
    updated_by              TEXT
);

CREATE INDEX IF NOT EXISTS idx_user_profiles_user_id ON public.user_profiles(user_id);

--rollback DROP INDEX IF EXISTS idx_user_profiles_user_id;
--rollback DROP TABLE IF EXISTS public.user_profiles;
