--
-- PostgreSQL database initialization script for E4U Lesson Service
-- Generated for testing purposes
-- Date: 2026-01-21
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';
SET default_table_access_method = heap;

-- ============================================
-- TABLE: user_unit_state
-- ============================================
CREATE TABLE IF NOT EXISTS public.user_unit_state (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    created_by character varying(255),
    deleted boolean NOT NULL DEFAULT false,
    deleted_at timestamp(6) with time zone,
    updated_at timestamp(6) with time zone,
    updated_by character varying(255),
    current_priority_score integer,
    difficulty_modifier real,
    is_fast_tracked boolean DEFAULT false,
    last_interaction_at timestamp(6) without time zone,
    proficiency_score real,
    status character varying(255) DEFAULT 'NOT_STARTED',
    unit_id uuid NOT NULL,
    user_id uuid NOT NULL,
    CONSTRAINT user_unit_state_pkey PRIMARY KEY (id),
    CONSTRAINT user_unit_state_status_check CHECK (((status)::text = ANY ((ARRAY['NOT_STARTED'::character varying, 'IN_PROGRESS'::character varying, 'COMPLETED'::character varying])::text[])))
);

-- ============================================
-- TABLE: user_vocab_instance
-- ============================================
CREATE TABLE IF NOT EXISTS public.user_vocab_instance (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    created_by character varying(255),
    deleted boolean NOT NULL DEFAULT false,
    deleted_at timestamp(6) with time zone,
    updated_at timestamp(6) with time zone,
    updated_by character varying(255),
    ai_reasoning character varying(255),
    context_sentence character varying(255),
    context_sentence_translation character varying(255),
    context_specific_word_meaning character varying(255),
    context_url character varying(255),
    frequency_count integer DEFAULT 0,
    is_goal_match boolean DEFAULT false,
    is_job_match boolean DEFAULT false,
    is_learning boolean DEFAULT false,
    is_mastered boolean DEFAULT false,
    media character varying(255),
    original_sentence character varying(255),
    relevance_score real,
    source_type character varying(255) DEFAULT 'EXTENSION',
    user_id uuid,
    weight_of_goal real,
    weight_of_interest real,
    word_lemma_id uuid,
    word_text character varying(255),
    CONSTRAINT user_vocab_instance_pkey PRIMARY KEY (id),
    CONSTRAINT user_vocab_instance_source_type_check CHECK (((source_type)::text = ANY ((ARRAY['EXTENSION'::character varying, 'AI_SUGGESTION'::character varying])::text[])))
);

-- ============================================
-- TABLE: dynamic_lesson
-- ============================================
CREATE TABLE IF NOT EXISTS public.dynamic_lesson (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    created_by character varying(255),
    deleted boolean NOT NULL DEFAULT false,
    deleted_at timestamp(6) with time zone,
    updated_at timestamp(6) with time zone,
    updated_by character varying(255),
    accuracy_rate real,
    completed_at timestamp(6) without time zone,
    completed_items integer DEFAULT 0,
    started_at timestamp(6) without time zone,
    status character varying(255) DEFAULT 'NOT_STARTED',
    total_items integer DEFAULT 0,
    user_id uuid NOT NULL,
    user_unit_state_id uuid,
    CONSTRAINT dynamic_lesson_pkey PRIMARY KEY (id),
    CONSTRAINT dynamic_lesson_status_check CHECK (((status)::text = ANY ((ARRAY['NOT_STARTED'::character varying, 'IN_PROGRESS'::character varying, 'COMPLETED'::character varying])::text[]))),
    CONSTRAINT fk_dynamic_lesson_user_unit_state FOREIGN KEY (user_unit_state_id) REFERENCES public.user_unit_state(id)
);

-- ============================================
-- TABLE: lesson_item
-- ============================================
CREATE TABLE IF NOT EXISTS public.lesson_item (
    created_at timestamp(6) with time zone NOT NULL,
    created_by character varying(255),
    deleted boolean NOT NULL DEFAULT false,
    deleted_at timestamp(6) with time zone,
    updated_at timestamp(6) with time zone,
    updated_by character varying(255),
    is_mastered_in_lesson boolean DEFAULT false,
    sequence_order integer,
    lesson_id uuid NOT NULL,
    word_instance_id uuid NOT NULL,
    CONSTRAINT lesson_item_pkey PRIMARY KEY (lesson_id, word_instance_id),
    CONSTRAINT fk_lesson_item_lesson FOREIGN KEY (lesson_id) REFERENCES public.dynamic_lesson(id),
    CONSTRAINT fk_lesson_item_word_instance FOREIGN KEY (word_instance_id) REFERENCES public.user_vocab_instance(id)
);

-- ============================================
-- TABLE: lesson_exercise
-- ============================================
CREATE TABLE IF NOT EXISTS public.lesson_exercise (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    created_by character varying(255),
    deleted boolean NOT NULL DEFAULT false,
    deleted_at timestamp(6) with time zone,
    updated_at timestamp(6) with time zone,
    updated_by character varying(255),
    exercise_data jsonb,
    exercise_type character varying(255) NOT NULL,
    sequence_order integer,
    word_instance_id uuid,
    is_completed boolean DEFAULT false,
    is_correct boolean,
    time_spent_seconds integer,
    user_answer character varying(255),
    lesson_id uuid,
    CONSTRAINT lesson_exercise_pkey PRIMARY KEY (id),
    CONSTRAINT lesson_exercise_exercise_type_check CHECK (((exercise_type)::text = ANY ((ARRAY['CONTEXTUAL_DISCOVERY'::character varying, 'MULTIPLE_CHOICE'::character varying, 'MECHANIC_DRILL'::character varying, 'MICRO_TASK_OUTPUT'::character varying, 'SENTENCE_BUILDING'::character varying, 'PARTIAL_OUTPUT'::character varying, 'CLOZE_WITH_AUDIO'::character varying])::text[]))),
    CONSTRAINT fk_lesson_exercise_word_instance FOREIGN KEY (word_instance_id) REFERENCES public.user_vocab_instance(id),
    CONSTRAINT fk_lesson_exercise_lesson FOREIGN KEY (lesson_id) REFERENCES public.dynamic_lesson(id)
);

-- ============================================
-- TABLE: vocab_assets
-- ============================================
CREATE TABLE IF NOT EXISTS public.vocab_assets (
    asset_id uuid NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    created_by character varying(255),
    deleted boolean NOT NULL DEFAULT false,
    deleted_at timestamp(6) with time zone,
    updated_at timestamp(6) with time zone,
    updated_by character varying(255),
    alt_text character varying(255),
    asset_type character varying(255) NOT NULL,
    sort_order integer,
    url character varying(255) NOT NULL,
    word_id uuid NOT NULL,
    CONSTRAINT vocab_assets_pkey PRIMARY KEY (asset_id),
    CONSTRAINT vocab_assets_asset_type_check CHECK (((asset_type)::text = ANY ((ARRAY['IMAGE'::character varying, 'AUDIO'::character varying, 'VIDEO'::character varying, 'TEXT'::character varying])::text[]))),
    CONSTRAINT fk_vocab_assets_word FOREIGN KEY (word_id) REFERENCES public.user_vocab_instance(id)
);

-- ============================================
-- INDEXES for better query performance
-- ============================================
CREATE INDEX IF NOT EXISTS idx_dynamic_lesson_user_id ON public.dynamic_lesson(user_id);
CREATE INDEX IF NOT EXISTS idx_dynamic_lesson_status ON public.dynamic_lesson(status);
CREATE INDEX IF NOT EXISTS idx_dynamic_lesson_user_unit_state_id ON public.dynamic_lesson(user_unit_state_id);
CREATE INDEX IF NOT EXISTS idx_lesson_exercise_lesson_id ON public.lesson_exercise(lesson_id);
CREATE INDEX IF NOT EXISTS idx_lesson_exercise_word_instance_id ON public.lesson_exercise(word_instance_id);
CREATE INDEX IF NOT EXISTS idx_lesson_exercise_exercise_type ON public.lesson_exercise(exercise_type);
CREATE INDEX IF NOT EXISTS idx_user_vocab_instance_user_id ON public.user_vocab_instance(user_id);
CREATE INDEX IF NOT EXISTS idx_user_vocab_instance_word_text ON public.user_vocab_instance(word_text);
CREATE INDEX IF NOT EXISTS idx_user_unit_state_user_id ON public.user_unit_state(user_id);
CREATE INDEX IF NOT EXISTS idx_user_unit_state_unit_id ON public.user_unit_state(unit_id);
