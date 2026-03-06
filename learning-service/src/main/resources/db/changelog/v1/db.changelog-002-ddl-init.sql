--liquibase formatted sql
--changeset system:ddl-init-001 comment:Create goal_definitions table

-- ============================================
-- Table: goal_definitions
-- Stores learning goal definitions with skills focus
-- ============================================
CREATE TABLE IF NOT EXISTS public.goal_definitions (
    id UUID NOT NULL PRIMARY KEY,
    goal_name VARCHAR(50) NOT NULL UNIQUE,
    skills_focus TEXT[],
    is_active BOOLEAN NOT NULL DEFAULT true,
    
    -- Audit fields (BaseEntity)
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at TIMESTAMP(6) WITH TIME ZONE
);

--changeset system:ddl-init-002 comment:Create global_dictionary table

-- ============================================
-- Table: global_dictionary
-- Central vocabulary dictionary with word metadata
-- ============================================
CREATE TABLE IF NOT EXISTS public.global_dictionary (
    id UUID NOT NULL PRIMARY KEY,
    lemma VARCHAR(100) NOT NULL,
    part_of_speech VARCHAR(20),
    definition TEXT,
    difficulty_level VARCHAR(5),
    frequency_score REAL,
    phonetic VARCHAR(255),
    audio_url VARCHAR(255),
    example_sentence TEXT,
    
    -- Audit fields
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at TIMESTAMP(6) WITH TIME ZONE
);

--changeset system:ddl-init-003 comment:Create curriculum table

-- ============================================
-- Table: curriculum
-- Stores curriculum definitions linked to goals
-- ============================================
CREATE TABLE IF NOT EXISTS public.curriculum (
    id UUID NOT NULL PRIMARY KEY,
    curriculum_name VARCHAR(100) NOT NULL,
    goal_id UUID REFERENCES public.goal_definitions(id),
    target_goals VARCHAR(255),
    description VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT true,
    
    -- Audit fields
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at TIMESTAMP(6) WITH TIME ZONE
);

--changeset system:ddl-init-004 comment:Create curriculum_units table

-- ============================================
-- Table: curriculum_units
-- Units within a curriculum
-- ============================================
CREATE TABLE IF NOT EXISTS public.curriculum_units (
    id UUID NOT NULL PRIMARY KEY,
    curriculum_id UUID REFERENCES public.curriculum(id),
    unit_name VARCHAR(100) NOT NULL,
    required_proficiency_level VARCHAR(5),
    default_order INTEGER,
    base_keywords TEXT[],
    description VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT true,
    
    -- Audit fields
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at TIMESTAMP(6) WITH TIME ZONE
);

--changeset system:ddl-init-005 comment:Create translation_dict table

-- ============================================
-- Table: translation_dict
-- Translations for dictionary words
-- ============================================
CREATE TABLE IF NOT EXISTS public.translation_dict (
    id UUID NOT NULL PRIMARY KEY,
    word_id UUID NOT NULL REFERENCES public.global_dictionary(id),
    dest_lang VARCHAR(10) NOT NULL,
    trans TEXT NOT NULL,
    example_translation TEXT,
    
    -- Audit fields
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at TIMESTAMP(6) WITH TIME ZONE,
    
    CONSTRAINT translation_dict_word_id_dest_lang_key UNIQUE (word_id, dest_lang)
);

--changeset system:ddl-init-006 comment:Create user_goals table

-- ============================================
-- Table: user_goals
-- Stores user's selected learning goals
-- ============================================
CREATE TABLE IF NOT EXISTS public.user_goals (
    user_id UUID NOT NULL,
    goal_id UUID NOT NULL REFERENCES public.goal_definitions(id),
    is_primary BOOLEAN NOT NULL DEFAULT false,
    started_at TIMESTAMP(6) WITH TIME ZONE,
    
    -- Audit fields
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at TIMESTAMP(6) WITH TIME ZONE,
    
    PRIMARY KEY (goal_id, user_id)
);

--changeset system:ddl-init-007 comment:Create word_context_templates table

-- ============================================
-- Table: word_context_templates
-- Contextual examples for vocabulary words
-- Supports both system-generated and user-specific contexts
-- ============================================
CREATE TABLE IF NOT EXISTS public.word_context_templates (
    id UUID NOT NULL PRIMARY KEY,
    unit_id UUID REFERENCES public.curriculum_units(id),
    word_id UUID NOT NULL REFERENCES public.global_dictionary(id),
    specific_meaning VARCHAR(255),
    context_sentence TEXT NOT NULL,
    context_translation TEXT,
    ai_reasoning TEXT,
    source_type VARCHAR(50) DEFAULT 'SYSTEM',
    created_for_user_id UUID,
    
    -- Audit fields
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at TIMESTAMP(6) WITH TIME ZONE,
    
    CONSTRAINT word_context_templates_source_type_check 
        CHECK (source_type IN ('SYSTEM', 'AI_GENERATED', 'USER_EXTENSION'))
);

--changeset system:ddl-init-008 comment:Create lesson_templates table

-- ============================================
-- Table: lesson_templates
-- Static lesson definitions within curriculum units
-- ============================================
CREATE TABLE IF NOT EXISTS public.lesson_templates (
    id UUID NOT NULL PRIMARY KEY,
    unit_id UUID NOT NULL REFERENCES public.curriculum_units(id),
    lesson_name VARCHAR(255) NOT NULL,
    lesson_type VARCHAR(50) DEFAULT 'STANDARD',
    sequence_order INTEGER DEFAULT 0,
    
    -- Audit fields
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at TIMESTAMP(6) WITH TIME ZONE,
    
    CONSTRAINT lesson_templates_lesson_type_check 
        CHECK (lesson_type IN ('STANDARD', 'REVIEW', 'ASSESSMENT', 'INTRODUCTION'))
);

--changeset system:ddl-init-009 comment:Create lesson_template_word_contexts join table

-- ============================================
-- Table: lesson_template_word_contexts
-- Junction table for ManyToMany between LessonTemplate and WordContextTemplate
-- ============================================
CREATE TABLE IF NOT EXISTS public.lesson_template_word_contexts (
    lesson_template_id UUID NOT NULL REFERENCES public.lesson_templates(id),
    word_context_template_id UUID NOT NULL REFERENCES public.word_context_templates(id),
    
    PRIMARY KEY (lesson_template_id, word_context_template_id)
);

--changeset system:ddl-init-010 comment:Create exercise_templates table

-- ============================================
-- Table: exercise_templates
-- Static exercise definitions for lessons
-- ============================================
CREATE TABLE IF NOT EXISTS public.exercise_templates (
    id UUID NOT NULL PRIMARY KEY,
    lesson_template_id UUID REFERENCES public.lesson_templates(id),
    word_context_template_id UUID REFERENCES public.word_context_templates(id),
    exercise_type VARCHAR(255) NOT NULL,
    exercise_payload JSONB NOT NULL,
    created_for_user_id UUID,
    
    -- Audit fields
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at TIMESTAMP(6) WITH TIME ZONE,
    
    CONSTRAINT exercise_templates_exercise_type_check 
        CHECK (exercise_type IN (
            'CONTEXTUAL_DISCOVERY', 
            'MULTIPLE_CHOICE', 
            'MECHANIC_DRILL', 
            'TARGET_WORD_INTEGRATION', 
            'SENTENCE_BUILDING', 
            'ASSISTED_COMPOSITION', 
            'CLOZE_WITH_AUDIO'
        ))
);

--changeset system:ddl-init-011 comment:Create user_unit_state table

-- ============================================
-- Table: user_unit_state
-- Tracks user's progress through curriculum units
-- ============================================
CREATE TABLE IF NOT EXISTS public.user_unit_state (
    id UUID NOT NULL PRIMARY KEY,
    unit_id UUID NOT NULL REFERENCES public.curriculum_units(id),
    user_id UUID NOT NULL,
    status VARCHAR(50) DEFAULT 'NOT_STARTED',
    proficiency_score REAL,
    difficulty_modifier REAL,
    current_priority_score INTEGER,
    is_fast_tracked BOOLEAN DEFAULT false,
    last_interaction_at TIMESTAMP(6) WITHOUT TIME ZONE,
    
    -- Audit fields
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at TIMESTAMP(6) WITH TIME ZONE,
    
    CONSTRAINT user_unit_state_status_check 
        CHECK (status IN ('NOT_STARTED', 'IN_PROGRESS', 'COMPLETED'))
);

--changeset system:ddl-init-012 comment:Create user_lesson_sessions table

-- ============================================
-- Table: user_lesson_sessions
-- Tracks user's progress through lesson templates
-- ============================================
CREATE TABLE IF NOT EXISTS public.user_lesson_sessions (
    id UUID NOT NULL PRIMARY KEY,
    lesson_template_id UUID NOT NULL REFERENCES public.lesson_templates(id),
    user_id UUID NOT NULL,
    user_unit_state_id UUID REFERENCES public.user_unit_state(id),
    status VARCHAR(50) DEFAULT 'NOT_STARTED',
    total_items INTEGER DEFAULT 0,
    completed_items INTEGER DEFAULT 0,
    correct_items INTEGER DEFAULT 0,
    accuracy_rate REAL,
    
    -- Audit fields
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at TIMESTAMP(6) WITH TIME ZONE,
    
    CONSTRAINT user_lesson_sessions_status_check 
        CHECK (status IN ('NOT_STARTED', 'IN_PROGRESS', 'COMPLETED', 'PAUSED'))
);

--changeset system:ddl-init-013 comment:Create user_exercise_attempts table

-- ============================================
-- Table: user_exercise_attempts
-- Records individual exercise attempts by users
-- ============================================
CREATE TABLE IF NOT EXISTS public.user_exercise_attempts (
    id UUID NOT NULL PRIMARY KEY,
    exercise_template_id UUID NOT NULL REFERENCES public.exercise_templates(id),
    session_id UUID NOT NULL REFERENCES public.user_lesson_sessions(id),
    user_answer JSONB,
    is_correct BOOLEAN,
    score INTEGER,
    time_taken_seconds INTEGER,
    
    -- Audit fields
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at TIMESTAMP(6) WITH TIME ZONE
);

--changeset system:ddl-init-014 comment:Create user_vocab_progress table

-- ============================================
-- Table: user_vocab_progress
-- Tracks user's vocabulary learning progress (SRS)
-- ============================================
CREATE TABLE IF NOT EXISTS public.user_vocab_progress (
    id UUID NOT NULL PRIMARY KEY,
    user_id UUID NOT NULL,
    word_id UUID NOT NULL REFERENCES public.global_dictionary(id),
    active_context_id UUID REFERENCES public.word_context_templates(id),
    is_mastered BOOLEAN DEFAULT false,
    relevance_score REAL,
    ease_factor REAL DEFAULT 2.5,
    interval_days INTEGER DEFAULT 1,
    consecutive_correct_answers INTEGER DEFAULT 0,
    next_review_at TIMESTAMP(6) WITH TIME ZONE,
    
    -- Audit fields
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at TIMESTAMP(6) WITH TIME ZONE,
    
    CONSTRAINT unique_user_word_progress UNIQUE (user_id, word_id)
);

--changeset system:ddl-init-015 comment:Create vocab_assets table

-- ============================================
-- Table: vocab_assets
-- Assets (images, audio, etc.) linked to word contexts
-- ============================================
CREATE TABLE IF NOT EXISTS public.vocab_assets (
    asset_id UUID NOT NULL PRIMARY KEY,
    word_context_template_id UUID NOT NULL REFERENCES public.word_context_templates(id),
    asset_type VARCHAR(50) NOT NULL,
    url VARCHAR(255) NOT NULL,
    alt_text VARCHAR(255),
    sort_order INTEGER DEFAULT 0,
    
    -- Audit fields
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP(6) WITH TIME ZONE,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT false,
    deleted_at TIMESTAMP(6) WITH TIME ZONE,
    
    CONSTRAINT vocab_assets_asset_type_check 
        CHECK (asset_type IN ('IMAGE', 'AUDIO', 'VIDEO', 'TEXT'))
);
