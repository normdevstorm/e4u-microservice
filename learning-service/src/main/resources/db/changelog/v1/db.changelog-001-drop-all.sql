--liquibase formatted sql
--changeset system:drop-all-001 comment:Drop all existing tables for clean migration

-- ============================================
-- DROP ALL EXISTING TABLES
-- Order matters due to foreign key constraints
-- ============================================

-- Drop dependent tables first (child tables)
DROP TABLE IF EXISTS public.vocab_assets CASCADE;
DROP TABLE IF EXISTS public.user_vocab_progress CASCADE;
DROP TABLE IF EXISTS public.user_exercise_attempts CASCADE;
DROP TABLE IF EXISTS public.user_lesson_sessions CASCADE;
DROP TABLE IF EXISTS public.user_unit_state CASCADE;
DROP TABLE IF EXISTS public.user_goals CASCADE;
DROP TABLE IF EXISTS public.lesson_template_word_contexts CASCADE;
DROP TABLE IF EXISTS public.exercise_templates CASCADE;
DROP TABLE IF EXISTS public.lesson_templates CASCADE;
DROP TABLE IF EXISTS public.word_context_templates CASCADE;
DROP TABLE IF EXISTS public.translation_dict CASCADE;
DROP TABLE IF EXISTS public.curriculum_units CASCADE;
DROP TABLE IF EXISTS public.curriculum CASCADE;
DROP TABLE IF EXISTS public.global_dictionary CASCADE;
DROP TABLE IF EXISTS public.goal_definitions CASCADE;

-- Drop legacy tables if they exist
DROP TABLE IF EXISTS public.units_base_words CASCADE;
DROP TABLE IF EXISTS public.lesson_item CASCADE;
DROP TABLE IF EXISTS public.lesson_exercise CASCADE;
DROP TABLE IF EXISTS public.dynamic_lesson CASCADE;
DROP TABLE IF EXISTS public.user_vocab_instance CASCADE;
