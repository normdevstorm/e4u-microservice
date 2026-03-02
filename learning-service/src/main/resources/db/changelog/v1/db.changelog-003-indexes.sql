--liquibase formatted sql
--changeset system:indexes-001 comment:Create indexes for all tables

-- ============================================
-- INDEXES for better query performance
-- ============================================

-- exercise_templates indexes
CREATE INDEX IF NOT EXISTS idx_exercise_lesson ON public.exercise_templates(lesson_template_id);
CREATE INDEX IF NOT EXISTS idx_exercise_word_context ON public.exercise_templates(word_context_template_id);
CREATE INDEX IF NOT EXISTS idx_exercise_user ON public.exercise_templates(created_for_user_id);

-- lesson_templates indexes
CREATE INDEX IF NOT EXISTS idx_lesson_unit ON public.lesson_templates(unit_id, sequence_order);

-- user_exercise_attempts indexes
CREATE INDEX IF NOT EXISTS idx_attempt_session ON public.user_exercise_attempts(session_id);
CREATE INDEX IF NOT EXISTS idx_attempt_exercise ON public.user_exercise_attempts(exercise_template_id);

-- user_lesson_sessions indexes
CREATE INDEX IF NOT EXISTS idx_session_user ON public.user_lesson_sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_session_lesson ON public.user_lesson_sessions(lesson_template_id);
CREATE INDEX IF NOT EXISTS idx_session_status ON public.user_lesson_sessions(status);

-- user_unit_state indexes
CREATE INDEX IF NOT EXISTS idx_user_unit_state_user ON public.user_unit_state(user_id);
CREATE INDEX IF NOT EXISTS idx_user_unit_state_unit ON public.user_unit_state(unit_id);

-- word_context_templates indexes
CREATE INDEX IF NOT EXISTS idx_context_lookup ON public.word_context_templates(word_id, created_for_user_id);
CREATE INDEX IF NOT EXISTS idx_context_unit ON public.word_context_templates(unit_id);
