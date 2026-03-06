--liquibase formatted sql
--changeset system:008-drop-unique-session-constraint comment:Remove unique constraint on (user_id, lesson_template_id) to allow multiple sessions per user per lesson

-- A user can now have multiple sessions for the same lesson (e.g., re-attempting after completion).
-- The application layer is responsible for selecting the most-relevant session.
ALTER TABLE public.user_lesson_sessions
    DROP CONSTRAINT IF EXISTS unique_user_lesson_session;
