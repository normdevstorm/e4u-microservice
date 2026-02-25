--liquibase formatted sql
--changeset system:260223-001 comment:Add attempt tracking fields to lesson_exercise table

-- ============================================
-- MIGRATION: Add attempt tracking columns
-- For exercise submission retry logic
-- ============================================

-- Add attempt_count column (tracks number of attempts made)
ALTER TABLE lesson_exercise 
ADD COLUMN IF NOT EXISTS attempt_count INTEGER DEFAULT 0;

-- Add max_attempts column (maximum attempts allowed per exercise)
ALTER TABLE lesson_exercise 
ADD COLUMN IF NOT EXISTS max_attempts INTEGER DEFAULT 3;

-- Add score column (points earned, 0-100)
ALTER TABLE lesson_exercise 
ADD COLUMN IF NOT EXISTS score INTEGER;

-- Update existing records to have default values
UPDATE lesson_exercise 
SET attempt_count = 0 
WHERE attempt_count IS NULL;

UPDATE lesson_exercise 
SET max_attempts = 3 
WHERE max_attempts IS NULL;

-- For completed exercises, set attempt_count to 1 if it was 0
UPDATE lesson_exercise 
SET attempt_count = 1 
WHERE is_completed = true AND attempt_count = 0;

--changeset system:260223-002 comment:Add last_activity_at and correct_items to dynamic_lesson

-- Add last_activity_at column for session resume tracking
ALTER TABLE dynamic_lesson 
ADD COLUMN IF NOT EXISTS last_activity_at TIMESTAMP WITH TIME ZONE;

-- Add correct_items column for accuracy tracking
ALTER TABLE dynamic_lesson 
ADD COLUMN IF NOT EXISTS correct_items INTEGER DEFAULT 0;

-- Initialize last_activity_at with started_at or created_at
UPDATE dynamic_lesson 
SET last_activity_at = COALESCE(started_at, created_at) 
WHERE last_activity_at IS NULL;

-- Initialize correct_items based on accuracy_rate if available
UPDATE dynamic_lesson 
SET correct_items = ROUND(COALESCE(accuracy_rate, 0) * COALESCE(completed_items, 0))
WHERE correct_items IS NULL OR correct_items = 0;
