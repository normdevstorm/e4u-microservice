--liquibase formatted sql
--changeset system:schema-001 comment:Create goal_definitions table with UUID

-- ============================================
-- Table: goal_definitions
-- Stores learning goal definitions with skills focus
-- ============================================
CREATE TABLE IF NOT EXISTS goal_definitions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    goal_name VARCHAR(50) NOT NULL UNIQUE,
    skills_focus TEXT[] NOT NULL,
    is_active BOOLEAN DEFAULT true,
    
    -- Audit fields (from BaseEntity)
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted BOOLEAN DEFAULT false,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_goal_definitions_goal_name ON goal_definitions(goal_name);
CREATE INDEX IF NOT EXISTS idx_goal_definitions_is_active ON goal_definitions(is_active);
CREATE INDEX IF NOT EXISTS idx_goal_definitions_deleted ON goal_definitions(deleted);

--changeset system:schema-002 comment:Create global_dictionary table

-- ============================================
-- Table: global_dictionary
-- Central vocabulary dictionary with word metadata
-- ============================================
CREATE TABLE IF NOT EXISTS global_dictionary (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lemma VARCHAR(100) NOT NULL,
    part_of_speech VARCHAR(20),
    definition TEXT,
    difficulty_level VARCHAR(5),
    frequency_score REAL,
    phonetic VARCHAR(255),
    audio_url TEXT,
    example_sentence TEXT,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted BOOLEAN DEFAULT false,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_global_dictionary_lemma ON global_dictionary(lemma);
CREATE INDEX IF NOT EXISTS idx_global_dictionary_part_of_speech ON global_dictionary(part_of_speech);
CREATE INDEX IF NOT EXISTS idx_global_dictionary_difficulty_level ON global_dictionary(difficulty_level);
CREATE INDEX IF NOT EXISTS idx_global_dictionary_deleted ON global_dictionary(deleted);
CREATE INDEX IF NOT EXISTS idx_global_dictionary_lemma_pos ON global_dictionary(lemma, part_of_speech);

--changeset system:schema-003 comment:Create user_goals table

-- ============================================
-- Table: user_goals
-- Stores user's selected learning goals (composite primary key)
-- ============================================
CREATE TABLE IF NOT EXISTS user_goals (
    user_id UUID NOT NULL,
    goal_id UUID NOT NULL REFERENCES goal_definitions(id),
    is_primary BOOLEAN DEFAULT false,
    started_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted BOOLEAN DEFAULT false,
    deleted_at TIMESTAMP WITH TIME ZONE,
    
    PRIMARY KEY (user_id, goal_id)
);

CREATE INDEX IF NOT EXISTS idx_user_goals_user_id ON user_goals(user_id);
CREATE INDEX IF NOT EXISTS idx_user_goals_goal_id ON user_goals(goal_id);
CREATE INDEX IF NOT EXISTS idx_user_goals_is_primary ON user_goals(is_primary);
CREATE INDEX IF NOT EXISTS idx_user_goals_deleted ON user_goals(deleted);

--changeset system:schema-004 comment:Create curriculum table

-- ============================================
-- Table: curriculum
-- Stores curriculum definitions linked to goals
-- ============================================
CREATE TABLE IF NOT EXISTS curriculum (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    curriculum_name VARCHAR(100) NOT NULL,
    goal_id UUID REFERENCES goal_definitions(id),
    target_goals TEXT,
    description TEXT,
    is_active BOOLEAN DEFAULT true,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted BOOLEAN DEFAULT false,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_curriculum_curriculum_name ON curriculum(curriculum_name);
CREATE INDEX IF NOT EXISTS idx_curriculum_goal_id ON curriculum(goal_id);
CREATE INDEX IF NOT EXISTS idx_curriculum_is_active ON curriculum(is_active);
CREATE INDEX IF NOT EXISTS idx_curriculum_deleted ON curriculum(deleted);

--changeset system:schema-005 comment:Create translation_dict table

-- ============================================
-- Table: translation_dict
-- Translations for dictionary words
-- ============================================
CREATE TABLE IF NOT EXISTS translation_dict (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    word_id UUID NOT NULL REFERENCES global_dictionary(id),
    dest_lang VARCHAR(10) NOT NULL,
    trans TEXT NOT NULL,
    example_translation TEXT,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted BOOLEAN DEFAULT false,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_translation_dict_word_id ON translation_dict(word_id);
CREATE INDEX IF NOT EXISTS idx_translation_dict_dest_lang ON translation_dict(dest_lang);
CREATE INDEX IF NOT EXISTS idx_translation_dict_deleted ON translation_dict(deleted);
CREATE UNIQUE INDEX IF NOT EXISTS idx_translation_dict_word_lang ON translation_dict(word_id, dest_lang) WHERE deleted = false;

--changeset system:schema-006 comment:Create curriculum_units table

-- ============================================
-- Table: curriculum_units
-- Units within a curriculum
-- ============================================
CREATE TABLE IF NOT EXISTS curriculum_units (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    curriculum_id UUID NOT NULL REFERENCES curriculum(id),
    unit_name VARCHAR(100) NOT NULL,
    required_proficiency_level VARCHAR(5),
    default_order INTEGER DEFAULT 0,
    base_keywords TEXT[],
    description TEXT,
    is_active BOOLEAN DEFAULT true,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted BOOLEAN DEFAULT false,
    deleted_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_curriculum_units_curriculum_id ON curriculum_units(curriculum_id);
CREATE INDEX IF NOT EXISTS idx_curriculum_units_unit_name ON curriculum_units(unit_name);
CREATE INDEX IF NOT EXISTS idx_curriculum_units_default_order ON curriculum_units(default_order);
CREATE INDEX IF NOT EXISTS idx_curriculum_units_deleted ON curriculum_units(deleted);

--changeset system:schema-007 comment:Create units_base_words table

-- ============================================
-- Table: units_base_words
-- Junction table linking units to vocabulary words (composite primary key)
-- ============================================
CREATE TABLE IF NOT EXISTS units_base_words (
    unit_id UUID NOT NULL REFERENCES curriculum_units(id),
    word_id UUID NOT NULL REFERENCES global_dictionary(id),
    sequence_order INTEGER DEFAULT 0,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted BOOLEAN DEFAULT false,
    deleted_at TIMESTAMP WITH TIME ZONE,
    
    PRIMARY KEY (unit_id, word_id)
);

CREATE INDEX IF NOT EXISTS idx_units_base_words_unit_id ON units_base_words(unit_id);
CREATE INDEX IF NOT EXISTS idx_units_base_words_word_id ON units_base_words(word_id);
CREATE INDEX IF NOT EXISTS idx_units_base_words_sequence_order ON units_base_words(sequence_order);
CREATE INDEX IF NOT EXISTS idx_units_base_words_deleted ON units_base_words(deleted);
