--liquibase formatted sql
--changeset system:210126-001 comment:Insert test user_unit_state records

-- ============================================
-- USER_UNIT_STATE - Test Data
-- ============================================
INSERT INTO user_unit_state (id, created_at, created_by, deleted, current_priority_score, difficulty_modifier, is_fast_tracked, last_interaction_at, proficiency_score, status, unit_id, user_id)
VALUES
    ('f47ac10b-58cc-4372-a567-0e02b2c3d479', '2026-01-21 10:00:00+00', 'system', false, 85, 1.0, false, '2026-01-21 10:00:00', 0.75, 'IN_PROGRESS', '3fa85f64-5717-4562-b3fc-2c963f66afa6', '550e8400-e29b-41d4-a716-446655440000'),
    ('6ba7b810-9dad-11d1-80b4-00c04fd430c8', '2026-01-21 10:00:00+00', 'system', false, 60, 0.8, true, '2026-01-20 15:30:00', 0.45, 'NOT_STARTED', '7c9e6679-7425-40de-944b-e07fc1f90ae7', '550e8400-e29b-41d4-a716-446655440000'),
    ('6ba7b811-9dad-11d1-80b4-00c04fd430c8', '2026-01-15 08:00:00+00', 'system', false, 95, 1.2, false, '2026-01-20 18:00:00', 0.92, 'COMPLETED', '9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d', '123e4567-e89b-12d3-a456-426614174000');

--changeset system:210126-002 comment:Insert test user_vocab_instance records

-- ============================================
-- USER_VOCAB_INSTANCE - Test Data (User A - Tech vocabulary)
-- ============================================
INSERT INTO user_vocab_instance (id, created_at, created_by, deleted, ai_reasoning, context_sentence, context_sentence_translation, context_specific_word_meaning, context_url, frequency_count, is_goal_match, is_job_match, is_learning, is_mastered, original_sentence, relevance_score, source_type, user_id, weight_of_goal, weight_of_interest, word_text)
VALUES
    -- User A: Tech vocabulary
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', '2026-01-20 09:00:00+00', 'system', false, 
     'High relevance to user tech job goals', 
     'The algorithm processes data efficiently.', 
     'Thuật toán xử lý dữ liệu hiệu quả.', 
     'A step-by-step procedure for calculations', 
     'https://example.com/tech-article', 
     5, true, true, true, false, 
     'The algorithm processes data efficiently.', 
     0.95, 'EXTENSION', '550e8400-e29b-41d4-a716-446655440000', 0.9, 0.8, 'algorithm'),

    ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', '2026-01-20 09:30:00+00', 'system', false, 
     'Common word in software development', 
     'We need to implement a new feature.', 
     'Chúng ta cần triển khai một tính năng mới.', 
     'To put into effect or action', 
     'https://example.com/dev-blog', 
     12, true, true, true, false, 
     'We need to implement a new feature.', 
     0.88, 'EXTENSION', '550e8400-e29b-41d4-a716-446655440000', 0.85, 0.75, 'implement'),

    ('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33', '2026-01-19 14:00:00+00', 'system', false, 
     'Suggested by AI based on learning pattern', 
     'The framework provides excellent scalability.', 
     'Khung làm việc cung cấp khả năng mở rộng tuyệt vời.', 
     'A basic structure underlying a system', 
     NULL, 
     3, true, true, true, false, 
     'The framework provides excellent scalability.', 
     0.82, 'AI_SUGGESTION', '550e8400-e29b-41d4-a716-446655440000', 0.7, 0.9, 'framework'),

    ('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a44', '2026-01-18 11:00:00+00', 'system', false, 
     'Essential programming concept', 
     'Variables store data temporarily in memory.', 
     'Biến lưu trữ dữ liệu tạm thời trong bộ nhớ.', 
     'A named storage location in programming', 
     'https://example.com/programming-basics', 
     25, true, true, false, true, 
     'Variables store data temporarily in memory.', 
     0.99, 'EXTENSION', '550e8400-e29b-41d4-a716-446655440000', 0.95, 0.85, 'variable'),

    -- User B: Business vocabulary
    ('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a55', '2026-01-17 16:00:00+00', 'system', false, 
     'Business vocabulary for meetings', 
     'Let us schedule a meeting to discuss the proposal.', 
     'Hãy lên lịch cuộc họp để thảo luận đề xuất.', 
     'To plan or arrange for a specific time', 
     'https://example.com/business-english', 
     8, true, true, true, false, 
     'Let us schedule a meeting to discuss the proposal.', 
     0.78, 'EXTENSION', '123e4567-e89b-12d3-a456-426614174000', 0.8, 0.6, 'schedule'),

    ('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a66', '2026-01-16 10:00:00+00', 'system', false, 
     'Key term for project management', 
     'The deadline for submission is next Friday.', 
     'Hạn chót nộp bài là thứ Sáu tuần sau.', 
     'The latest time by which something should be completed', 
     NULL, 
     6, true, true, true, false, 
     'The deadline for submission is next Friday.', 
     0.85, 'AI_SUGGESTION', '123e4567-e89b-12d3-a456-426614174000', 0.75, 0.7, 'deadline');

--changeset system:210126-003 comment:Insert test dynamic_lesson records

-- ============================================
-- DYNAMIC_LESSON - Test Data
-- ============================================
INSERT INTO dynamic_lesson (id, created_at, created_by, deleted, accuracy_rate, completed_at, completed_items, started_at, status, total_items, user_id, user_unit_state_id)
VALUES
    -- Lesson 1: In Progress for User A
    ('1b9d6bcd-bbfd-4b2d-9b5d-ab8dfbbd4bed', '2026-01-21 10:00:00+00', 'system', false, 
     0.75, NULL, 3, '2026-01-21 10:05:00', 'IN_PROGRESS', 5, 
     '550e8400-e29b-41d4-a716-446655440000', 'f47ac10b-58cc-4372-a567-0e02b2c3d479'),

    -- Lesson 2: Completed for User A
    ('2b9d6bcd-bbfd-4b2d-9b5d-ab8dfbbd4bed', '2026-01-20 14:00:00+00', 'system', false, 
     0.90, '2026-01-20 14:45:00', 4, '2026-01-20 14:00:00', 'COMPLETED', 4, 
     '550e8400-e29b-41d4-a716-446655440000', 'f47ac10b-58cc-4372-a567-0e02b2c3d479'),

    -- Lesson 3: Not Started for User A
    ('3b9d6bcd-bbfd-4b2d-9b5d-ab8dfbbd4bed', '2026-01-21 11:00:00+00', 'system', false, 
     NULL, NULL, 0, NULL, 'NOT_STARTED', 6, 
     '550e8400-e29b-41d4-a716-446655440000', '6ba7b810-9dad-11d1-80b4-00c04fd430c8'),

    -- Lesson 4: In Progress for User B
    ('4b9d6bcd-bbfd-4b2d-9b5d-ab8dfbbd4bed', '2026-01-21 09:00:00+00', 'system', false, 
     0.60, NULL, 2, '2026-01-21 09:10:00', 'IN_PROGRESS', 4, 
     '123e4567-e89b-12d3-a456-426614174000', '6ba7b811-9dad-11d1-80b4-00c04fd430c8');

--changeset system:210126-004 comment:Insert test lesson_item records

-- ============================================
-- LESSON_ITEM - Test Data
-- ============================================
INSERT INTO lesson_item (created_at, created_by, deleted, is_mastered_in_lesson, sequence_order, lesson_id, word_instance_id)
VALUES
    -- Lesson 1 items (User A)
    ('2026-01-21 10:00:00+00', 'system', false, true, 1, '1b9d6bcd-bbfd-4b2d-9b5d-ab8dfbbd4bed', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),
    ('2026-01-21 10:00:00+00', 'system', false, true, 2, '1b9d6bcd-bbfd-4b2d-9b5d-ab8dfbbd4bed', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22'),
    ('2026-01-21 10:00:00+00', 'system', false, false, 3, '1b9d6bcd-bbfd-4b2d-9b5d-ab8dfbbd4bed', 'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33'),

    -- Lesson 2 items (User A - completed)
    ('2026-01-20 14:00:00+00', 'system', false, true, 1, '2b9d6bcd-bbfd-4b2d-9b5d-ab8dfbbd4bed', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a44'),

    -- Lesson 4 items (User B)
    ('2026-01-21 09:00:00+00', 'system', false, true, 1, '4b9d6bcd-bbfd-4b2d-9b5d-ab8dfbbd4bed', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a55'),
    ('2026-01-21 09:00:00+00', 'system', false, false, 2, '4b9d6bcd-bbfd-4b2d-9b5d-ab8dfbbd4bed', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a66');

--changeset system:210126-005 comment:Insert test lesson_exercise records

-- ============================================
-- LESSON_EXERCISE - Test Data
-- ============================================
INSERT INTO lesson_exercise (id, created_at, created_by, deleted, exercise_type, exercise_data, sequence_order, word_instance_id, lesson_id, is_completed, is_correct, user_answer, time_spent_seconds)
VALUES
    -- Exercises for Lesson 1 - word "algorithm"
    ('e1a2b3c4-d5e6-4f7a-8b9c-0d1e2f3a4b5c', '2026-01-21 10:00:00+00', 'system', false, 
     'CONTEXTUAL_DISCOVERY',
     '{"type": "CONTEXTUAL_DISCOVERY", "sentence": "The algorithm processes data efficiently.", "targetWord": "algorithm", "targetWordIndex": 1, "audioUrl": "https://cdn.e4u.com/audio/algorithm.mp3", "imageUrl": "https://cdn.e4u.com/images/algorithm.jpg", "hint": "A step-by-step procedure"}'::jsonb,
     1, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', '1b9d6bcd-bbfd-4b2d-9b5d-ab8dfbbd4bed',
     true, true, 'algorithm', 25),

    ('e2a2b3c4-d5e6-4f7a-8b9c-0d1e2f3a4b5c', '2026-01-21 10:00:00+00', 'system', false, 
     'MULTIPLE_CHOICE',
     '{"type": "MULTIPLE_CHOICE", "question": "What does algorithm mean?", "options": ["A step-by-step procedure", "A type of database", "A programming language", "A network protocol"], "correctOptionIndex": 0, "explanation": "An algorithm is a step-by-step procedure for calculations."}'::jsonb,
     2, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', '1b9d6bcd-bbfd-4b2d-9b5d-ab8dfbbd4bed',
     true, true, '0', 18),

    -- Exercises for Lesson 1 - word "implement"
    ('e3a2b3c4-d5e6-4f7a-8b9c-0d1e2f3a4b5c', '2026-01-21 10:00:00+00', 'system', false, 
     'MECHANIC_DRILL',
     '{"type": "MECHANIC_DRILL", "promptText": "Type the word: implement", "targetWord": "implement", "attempts": 3, "successThreshold": 2}'::jsonb,
     3, 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', '1b9d6bcd-bbfd-4b2d-9b5d-ab8dfbbd4bed',
     true, false, 'implment', 35),

    ('e4a2b3c4-d5e6-4f7a-8b9c-0d1e2f3a4b5c', '2026-01-21 10:00:00+00', 'system', false, 
     'SENTENCE_BUILDING',
     '{"type": "SENTENCE_BUILDING", "shuffledWords": ["to", "need", "We", "implement", "feature", "a", "new"], "correctSentence": "We need to implement a new feature", "targetWord": "implement"}'::jsonb,
     4, 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', '1b9d6bcd-bbfd-4b2d-9b5d-ab8dfbbd4bed',
     false, NULL, NULL, NULL),

    -- Exercises for Lesson 1 - word "framework" (not started)
    ('e5a2b3c4-d5e6-4f7a-8b9c-0d1e2f3a4b5c', '2026-01-21 10:00:00+00', 'system', false, 
     'MICRO_TASK_OUTPUT',
     '{"type": "MICRO_TASK_OUTPUT", "prompt": "Use framework in a sentence about web development", "targetWord": "framework", "minWords": 5, "maxWords": 15, "exampleResponse": "Spring Boot is a popular Java framework."}'::jsonb,
     5, 'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33', '1b9d6bcd-bbfd-4b2d-9b5d-ab8dfbbd4bed',
     false, NULL, NULL, NULL),

    -- Exercises for Lesson 4 (User B) - word "schedule"
    ('e6a2b3c4-d5e6-4f7a-8b9c-0d1e2f3a4b5c', '2026-01-21 09:00:00+00', 'system', false, 
     'CONTEXTUAL_DISCOVERY',
     '{"type": "CONTEXTUAL_DISCOVERY", "sentence": "Let us schedule a meeting to discuss the proposal.", "targetWord": "schedule", "targetWordIndex": 2, "audioUrl": "https://cdn.e4u.com/audio/schedule.mp3", "hint": "To plan for a specific time"}'::jsonb,
     1, 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a55', '4b9d6bcd-bbfd-4b2d-9b5d-ab8dfbbd4bed',
     true, true, 'schedule', 20),

    ('e7a2b3c4-d5e6-4f7a-8b9c-0d1e2f3a4b5c', '2026-01-21 09:00:00+00', 'system', false, 
     'MULTIPLE_CHOICE',
     '{"type": "MULTIPLE_CHOICE", "question": "Which sentence uses schedule correctly?", "options": ["I will schedule the meeting for tomorrow", "The schedule ran very fast", "Please schedule me the book", "They schedule the mountain"], "correctOptionIndex": 0, "explanation": "Schedule means to plan for a specific time."}'::jsonb,
     2, 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a55', '4b9d6bcd-bbfd-4b2d-9b5d-ab8dfbbd4bed',
     true, false, '2', 45),

    -- Exercises for Lesson 4 (User B) - word "deadline" (not started)
    ('e8a2b3c4-d5e6-4f7a-8b9c-0d1e2f3a4b5c', '2026-01-21 09:00:00+00', 'system', false, 
     'SENTENCE_BUILDING',
     '{"type": "SENTENCE_BUILDING", "shuffledWords": ["deadline", "The", "submission", "for", "Friday", "next", "is"], "correctSentence": "The deadline for submission is next Friday", "targetWord": "deadline"}'::jsonb,
     3, 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a66', '4b9d6bcd-bbfd-4b2d-9b5d-ab8dfbbd4bed',
     false, NULL, NULL, NULL);

--changeset system:210126-006 comment:Insert test vocab_assets records

-- ============================================
-- VOCAB_ASSETS - Test Data
-- ============================================
INSERT INTO vocab_assets (asset_id, created_at, created_by, deleted, alt_text, asset_type, sort_order, url, word_id)
VALUES
    -- Assets for "algorithm"
    ('a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', '2026-01-20 09:00:00+00', 'system', false, 
     'Flowchart representing an algorithm', 'IMAGE', 1, 
     'https://cdn.e4u.com/images/algorithm-flowchart.jpg', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),

    ('a2b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', '2026-01-20 09:00:00+00', 'system', false, 
     'Pronunciation of algorithm', 'AUDIO', 2, 
     'https://cdn.e4u.com/audio/algorithm.mp3', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'),

    -- Assets for "implement"
    ('a3b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', '2026-01-20 09:30:00+00', 'system', false, 
     'Developer implementing code', 'IMAGE', 1, 
     'https://cdn.e4u.com/images/implement-code.jpg', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22'),

    -- Assets for "schedule"
    ('a4b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', '2026-01-17 16:00:00+00', 'system', false, 
     'Calendar showing scheduled meetings', 'IMAGE', 1, 
     'https://cdn.e4u.com/images/schedule-calendar.jpg', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a55'),

    ('a5b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', '2026-01-17 16:00:00+00', 'system', false, 
     'Pronunciation of schedule', 'AUDIO', 2, 
     'https://cdn.e4u.com/audio/schedule.mp3', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a55');
