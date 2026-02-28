--liquibase formatted sql
--changeset system:260222-001 comment:Add new exercise types ASSISTED_COMPOSITION and TARGET_WORD_INTEGRATION

-- ============================================
-- MIGRATION: Add new exercise types to constraint
-- PARTIAL_OUTPUT -> ASSISTED_COMPOSITION (new name)
-- MICRO_TASK_OUTPUT -> TARGET_WORD_INTEGRATION (new name)
-- 
-- NOTE: We keep legacy type names in the constraint for backward compatibility.
-- The Java code (ExerciseData.java) has @JsonSubTypes mappings for both old and new
-- type names, so existing records with old type names will still deserialize correctly.
-- New records will use the new type names.
-- ============================================

-- Step 1: Drop the old constraint
ALTER TABLE lesson_exercise DROP CONSTRAINT IF EXISTS lesson_exercise_exercise_type_check;

-- Step 2: Add the new constraint with both old and new types (for backward compatibility)
ALTER TABLE lesson_exercise ADD CONSTRAINT lesson_exercise_exercise_type_check 
CHECK (((exercise_type)::text = ANY ((ARRAY[
    'CONTEXTUAL_DISCOVERY'::character varying, 
    'MULTIPLE_CHOICE'::character varying, 
    'MECHANIC_DRILL'::character varying, 
    'TARGET_WORD_INTEGRATION'::character varying, 
    'SENTENCE_BUILDING'::character varying, 
    'ASSISTED_COMPOSITION'::character varying, 
    'CLOZE_WITH_AUDIO'::character varying,
    -- Legacy types for backward compatibility (existing data keeps working)
    'MICRO_TASK_OUTPUT'::character varying, 
    'PARTIAL_OUTPUT'::character varying
])::text[])));

-- NOTE: Existing records are NOT migrated. They keep their old type names.
-- This is safe because ExerciseData.java maps both old and new type names 
-- to the same Java classes:
--   "MICRO_TASK_OUTPUT" -> TargetWordIntegrationExerciseData
--   "TARGET_WORD_INTEGRATION" -> TargetWordIntegrationExerciseData
--   "PARTIAL_OUTPUT" -> AssistedCompositionExerciseData  
--   "ASSISTED_COMPOSITION" -> AssistedCompositionExerciseData
