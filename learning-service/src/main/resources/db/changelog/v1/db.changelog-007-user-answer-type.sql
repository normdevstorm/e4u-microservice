--liquibase formatted sql

--changeset system:ddl-fix-007-user-answer-type comment:Backfill missing ExerciseAnswer.type discriminator in user_exercise_attempts.user_answer

-- Ensure existing attempt payloads always have a "type" field so that
-- Jackson can deserialize ExerciseAnswer polymorphically without legacy fallbacks.
--
-- We derive the type from exercise_templates.exercise_type.

UPDATE public.user_exercise_attempts ua
SET user_answer = (
    CASE
      WHEN ua.user_answer IS NULL
        THEN '{}'::jsonb
      WHEN jsonb_typeof(ua.user_answer) = 'object'
        THEN ua.user_answer
      ELSE jsonb_build_object('raw', ua.user_answer)
      END
    ) || jsonb_build_object('type', et.exercise_type)
FROM public.exercise_templates et
WHERE ua.exercise_template_id = et.id
  AND et.exercise_type IS NOT NULL
  AND (ua.user_answer IS NULL OR ua.user_answer->>'type' IS NULL);
