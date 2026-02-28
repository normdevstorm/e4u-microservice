--liquibase formatted sql
--changeset system:data-001 comment:Insert goal_definitions

-- ============================================
-- GOAL_DEFINITIONS - Test Data
-- ============================================
INSERT INTO goal_definitions (id, created_at, created_by, deleted, goal_name, skills_focus, is_active)
VALUES
    ('10000000-0000-0000-0000-000000000001', '2026-01-21 10:00:00+00', 'system', false, 'Tech Professional', ARRAY['technical vocabulary', 'business communication', 'presentation skills'], true),
    ('10000000-0000-0000-0000-000000000002', '2026-01-21 10:00:00+00', 'system', false, 'Business Communication', ARRAY['negotiation', 'email writing', 'meeting skills', 'report writing'], true),
    ('10000000-0000-0000-0000-000000000003', '2026-01-21 10:00:00+00', 'system', false, 'IELTS Preparation', ARRAY['academic writing', 'speaking fluency', 'listening comprehension', 'reading skills'], true),
    ('10000000-0000-0000-0000-000000000004', '2026-01-21 10:00:00+00', 'system', false, 'Daily Conversation', ARRAY['casual conversation', 'idioms', 'slang', 'pronunciation'], true),
    ('10000000-0000-0000-0000-000000000005', '2026-01-21 10:00:00+00', 'system', false, 'Academic English', ARRAY['research writing', 'critical thinking', 'academic vocabulary', 'citation skills'], true)
ON CONFLICT (id) DO NOTHING;

--changeset system:data-002 comment:Insert global_dictionary

-- ============================================
-- GLOBAL_DICTIONARY - Test Data
-- ============================================
INSERT INTO global_dictionary (id, created_at, created_by, deleted, lemma, part_of_speech, definition, difficulty_level, frequency_score, phonetic, audio_url, example_sentence)
VALUES
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', '2026-01-15 08:00:00+00', 'system', false, 'algorithm', 'noun', 'A step-by-step procedure for calculations or problem-solving', '6', 0.7523, '/ˈælɡəˌrɪðəm/', 'https://cdn.e4u.com/audio/algorithm.mp3', 'The search algorithm efficiently finds the shortest path.'),
    ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', '2026-01-15 08:00:00+00', 'system', false, 'implement', 'verb', 'To put into effect or action; to carry out', '5', 0.8234, '/ˈɪmpləˌmɛnt/', 'https://cdn.e4u.com/audio/implement.mp3', 'We need to implement the new security features.'),
    ('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33', '2026-01-15 08:00:00+00', 'system', false, 'framework', 'noun', 'A basic structure underlying a system or concept', '5', 0.7891, '/ˈfreɪmˌwɜrk/', 'https://cdn.e4u.com/audio/framework.mp3', 'Spring Boot is a popular Java framework.'),
    ('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a44', '2026-01-15 08:00:00+00', 'system', false, 'variable', 'noun', 'A named storage location in programming that holds a value', '4', 0.9012, '/ˈvɛriəbəl/', 'https://cdn.e4u.com/audio/variable.mp3', 'Declare a variable before using it in your code.'),
    ('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a55', '2026-01-15 08:00:00+00', 'system', false, 'schedule', 'verb', 'To plan or arrange for a specific time', '3', 0.9234, '/ˈskɛdʒuːl/', 'https://cdn.e4u.com/audio/schedule.mp3', 'Let us schedule a meeting for next Monday.'),
    ('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a66', '2026-01-15 08:00:00+00', 'system', false, 'deadline', 'noun', 'The latest time by which something should be completed', '3', 0.8765, '/ˈdɛdˌlaɪn/', 'https://cdn.e4u.com/audio/deadline.mp3', 'The project deadline is next Friday.'),
    ('11111111-1111-1111-1111-111111111111', '2026-01-15 08:00:00+00', 'system', false, 'database', 'noun', 'An organized collection of structured information or data', '5', 0.8456, '/ˈdeɪtəˌbeɪs/', 'https://cdn.e4u.com/audio/database.mp3', 'The application stores user data in a database.'),
    ('22222222-2222-2222-2222-222222222222', '2026-01-15 08:00:00+00', 'system', false, 'interface', 'noun', 'A point where two systems or subjects meet and interact', '5', 0.7654, '/ˈɪntərˌfeɪs/', 'https://cdn.e4u.com/audio/interface.mp3', 'Design a user-friendly interface for the application.'),
    ('33333333-3333-3333-3333-333333333333', '2026-01-15 08:00:00+00', 'system', false, 'negotiate', 'verb', 'To discuss something in order to reach an agreement', '5', 0.7234, '/nɪˈɡoʊʃiˌeɪt/', 'https://cdn.e4u.com/audio/negotiate.mp3', 'We need to negotiate the contract terms.'),
    ('44444444-4444-4444-4444-444444444444', '2026-01-15 08:00:00+00', 'system', false, 'stakeholder', 'noun', 'A person with an interest or concern in something', '6', 0.6987, '/ˈsteɪkˌhoʊldər/', 'https://cdn.e4u.com/audio/stakeholder.mp3', 'All stakeholders must approve the project plan.'),
    ('55555555-5555-5555-5555-555555555555', '2026-01-15 08:00:00+00', 'system', false, 'collaborate', 'verb', 'To work jointly with others on a task or project', '4', 0.8123, '/kəˈlæbəˌreɪt/', 'https://cdn.e4u.com/audio/collaborate.mp3', 'Teams collaborate using video conferencing tools.'),
    ('66666666-6666-6666-6666-666666666666', '2026-01-15 08:00:00+00', 'system', false, 'optimize', 'verb', 'To make the best or most effective use of', '5', 0.7456, '/ˈɑptəˌmaɪz/', 'https://cdn.e4u.com/audio/optimize.mp3', 'We need to optimize the query performance.')
ON CONFLICT (id) DO NOTHING;

--changeset system:data-003 comment:Insert user_goals

-- ============================================
-- USER_GOALS - Test Data
-- User A: 550e8400-e29b-41d4-a716-446655440000
-- User B: 123e4567-e89b-12d3-a456-426614174000
-- ============================================
INSERT INTO user_goals (user_id, goal_id, is_primary, started_at, created_at, created_by, deleted)
VALUES
    ('550e8400-e29b-41d4-a716-446655440000', '10000000-0000-0000-0000-000000000001', true, '2026-01-15 08:00:00+00', '2026-01-15 08:00:00+00', 'system', false),
    ('550e8400-e29b-41d4-a716-446655440000', '10000000-0000-0000-0000-000000000004', false, '2026-01-16 09:00:00+00', '2026-01-16 09:00:00+00', 'system', false),
    ('123e4567-e89b-12d3-a456-426614174000', '10000000-0000-0000-0000-000000000002', true, '2026-01-10 10:00:00+00', '2026-01-10 10:00:00+00', 'system', false),
    ('123e4567-e89b-12d3-a456-426614174000', '10000000-0000-0000-0000-000000000003', false, '2026-01-12 14:00:00+00', '2026-01-12 14:00:00+00', 'system', false)
ON CONFLICT (user_id, goal_id) DO NOTHING;

--changeset system:data-004 comment:Insert translation_dict

-- ============================================
-- TRANSLATION_DICT - Vietnamese translations
-- ============================================
INSERT INTO translation_dict (id, word_id, dest_lang, trans, example_translation, created_at, created_by, deleted)
VALUES
    ('20000000-0000-0000-0000-000000000001', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'vi', 'thuật toán', 'Thuật toán tìm kiếm hiệu quả tìm đường đi ngắn nhất.', '2026-01-15 08:00:00+00', 'system', false),
    ('20000000-0000-0000-0000-000000000002', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'vi', 'triển khai', 'Chúng ta cần triển khai các tính năng bảo mật mới.', '2026-01-15 08:00:00+00', 'system', false),
    ('20000000-0000-0000-0000-000000000003', 'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33', 'vi', 'khung làm việc', 'Spring Boot là một khung làm việc Java phổ biến.', '2026-01-15 08:00:00+00', 'system', false),
    ('20000000-0000-0000-0000-000000000004', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a44', 'vi', 'biến', 'Khai báo biến trước khi sử dụng trong mã của bạn.', '2026-01-15 08:00:00+00', 'system', false),
    ('20000000-0000-0000-0000-000000000005', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a55', 'vi', 'lên lịch', 'Hãy lên lịch cuộc họp vào thứ Hai tuần tới.', '2026-01-15 08:00:00+00', 'system', false),
    ('20000000-0000-0000-0000-000000000006', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a66', 'vi', 'hạn chót', 'Hạn chót của dự án là thứ Sáu tuần sau.', '2026-01-15 08:00:00+00', 'system', false),
    ('20000000-0000-0000-0000-000000000007', '11111111-1111-1111-1111-111111111111', 'vi', 'cơ sở dữ liệu', 'Ứng dụng lưu trữ dữ liệu người dùng trong cơ sở dữ liệu.', '2026-01-15 08:00:00+00', 'system', false),
    ('20000000-0000-0000-0000-000000000008', '22222222-2222-2222-2222-222222222222', 'vi', 'giao diện', 'Thiết kế giao diện thân thiện với người dùng cho ứng dụng.', '2026-01-15 08:00:00+00', 'system', false),
    ('20000000-0000-0000-0000-000000000009', '33333333-3333-3333-3333-333333333333', 'vi', 'đàm phán', 'Chúng ta cần đàm phán các điều khoản hợp đồng.', '2026-01-15 08:00:00+00', 'system', false),
    ('20000000-0000-0000-0000-000000000010', '44444444-4444-4444-4444-444444444444', 'vi', 'các bên liên quan', 'Tất cả các bên liên quan phải phê duyệt kế hoạch dự án.', '2026-01-15 08:00:00+00', 'system', false),
    ('20000000-0000-0000-0000-000000000011', '55555555-5555-5555-5555-555555555555', 'vi', 'cộng tác', 'Các nhóm cộng tác bằng công cụ hội nghị video.', '2026-01-15 08:00:00+00', 'system', false),
    ('20000000-0000-0000-0000-000000000012', '66666666-6666-6666-6666-666666666666', 'vi', 'tối ưu hóa', 'Chúng ta cần tối ưu hóa hiệu suất truy vấn.', '2026-01-15 08:00:00+00', 'system', false)
ON CONFLICT (id) DO NOTHING;

--changeset system:data-005 comment:Insert curriculum

-- ============================================
-- CURRICULUM - Test Data
-- ============================================
INSERT INTO curriculum (id, created_at, created_by, deleted, curriculum_name, goal_id, target_goals, description, is_active)
VALUES
    ('30000000-0000-0000-0000-000000000001', '2026-01-15 08:00:00+00', 'system', false, 'Software Development English', '10000000-0000-0000-0000-000000000001', 'Master technical vocabulary for software development', 'Comprehensive curriculum for software developers', true),
    ('30000000-0000-0000-0000-000000000002', '2026-01-15 08:00:00+00', 'system', false, 'IT Project Management', '10000000-0000-0000-0000-000000000001', 'Learn project management communication skills', 'Communication skills for IT project managers', true),
    ('30000000-0000-0000-0000-000000000003', '2026-01-15 08:00:00+00', 'system', false, 'Business Meetings Mastery', '10000000-0000-0000-0000-000000000002', 'Excel in business meetings and presentations', 'Master business meeting communication', true),
    ('30000000-0000-0000-0000-000000000004', '2026-01-15 08:00:00+00', 'system', false, 'Professional Email Writing', '10000000-0000-0000-0000-000000000002', 'Write effective professional emails', 'Professional email writing skills', true),
    ('30000000-0000-0000-0000-000000000005', '2026-01-15 08:00:00+00', 'system', false, 'Everyday English Basics', '10000000-0000-0000-0000-000000000004', 'Build foundation for daily conversations', 'Foundation for everyday English', true)
ON CONFLICT (id) DO NOTHING;

--changeset system:data-006 comment:Insert curriculum_units

-- ============================================
-- CURRICULUM_UNITS - Test Data
-- ============================================
INSERT INTO curriculum_units (id, created_at, created_by, deleted, curriculum_id, unit_name, required_proficiency_level, default_order, base_keywords, description, is_active)
VALUES
    ('40000000-0000-0000-0000-000000000001', '2026-01-15 08:00:00+00', 'system', false, '30000000-0000-0000-0000-000000000001', 'Programming Fundamentals', '3', 1, ARRAY['variable', 'function', 'loop', 'condition'], 'Learn basic programming terminology', true),
    ('40000000-0000-0000-0000-000000000002', '2026-01-15 08:00:00+00', 'system', false, '30000000-0000-0000-0000-000000000001', 'Data Structures', '5', 2, ARRAY['array', 'list', 'tree', 'graph', 'algorithm'], 'Master data structure vocabulary', true),
    ('40000000-0000-0000-0000-000000000003', '2026-01-15 08:00:00+00', 'system', false, '30000000-0000-0000-0000-000000000001', 'Software Architecture', '6', 3, ARRAY['framework', 'pattern', 'design', 'architecture'], 'Architecture and design patterns', true),
    ('40000000-0000-0000-0000-000000000004', '2026-01-15 08:00:00+00', 'system', false, '30000000-0000-0000-0000-000000000001', 'Database Management', '5', 4, ARRAY['database', 'query', 'schema', 'index'], 'Database terminology and concepts', true),
    ('40000000-0000-0000-0000-000000000005', '2026-01-15 08:00:00+00', 'system', false, '30000000-0000-0000-0000-000000000002', 'Agile Methodology', '4', 1, ARRAY['sprint', 'backlog', 'scrum', 'kanban'], 'Agile project management terms', true),
    ('40000000-0000-0000-0000-000000000006', '2026-01-15 08:00:00+00', 'system', false, '30000000-0000-0000-0000-000000000002', 'Team Collaboration', '3', 2, ARRAY['collaborate', 'communicate', 'coordinate', 'delegate'], 'Team collaboration vocabulary', true),
    ('40000000-0000-0000-0000-000000000007', '2026-01-15 08:00:00+00', 'system', false, '30000000-0000-0000-0000-000000000003', 'Meeting Basics', '2', 1, ARRAY['schedule', 'agenda', 'minutes', 'action items'], 'Basic meeting terminology', true),
    ('40000000-0000-0000-0000-000000000008', '2026-01-15 08:00:00+00', 'system', false, '30000000-0000-0000-0000-000000000003', 'Negotiation Skills', '5', 2, ARRAY['negotiate', 'compromise', 'proposal', 'agreement'], 'Negotiation vocabulary', true),
    ('40000000-0000-0000-0000-000000000009', '2026-01-15 08:00:00+00', 'system', false, '30000000-0000-0000-0000-000000000003', 'Stakeholder Management', '6', 3, ARRAY['stakeholder', 'expectation', 'communication', 'alignment'], 'Stakeholder management terms', true),
    ('40000000-0000-0000-0000-000000000010', '2026-01-15 08:00:00+00', 'system', false, '30000000-0000-0000-0000-000000000004', 'Email Etiquette', '2', 1, ARRAY['greeting', 'closing', 'tone', 'formality'], 'Email etiquette basics', true),
    ('40000000-0000-0000-0000-000000000011', '2026-01-15 08:00:00+00', 'system', false, '30000000-0000-0000-0000-000000000004', 'Project Updates', '3', 2, ARRAY['deadline', 'progress', 'milestone', 'status'], 'Project update vocabulary', true),
    ('40000000-0000-0000-0000-000000000012', '2026-01-15 08:00:00+00', 'system', false, '30000000-0000-0000-0000-000000000005', 'Greetings and Introductions', '1', 1, ARRAY['hello', 'goodbye', 'introduce', 'meet'], 'Basic greetings and introductions', true)
ON CONFLICT (id) DO NOTHING;

--changeset system:data-007 comment:Insert units_base_words

-- ============================================
-- UNITS_BASE_WORDS - Test Data
-- ============================================
INSERT INTO units_base_words (unit_id, word_id, sequence_order, created_at, created_by, deleted)
VALUES
    ('40000000-0000-0000-0000-000000000001', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a44', 1, '2026-01-15 08:00:00+00', 'system', false),
    ('40000000-0000-0000-0000-000000000002', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 1, '2026-01-15 08:00:00+00', 'system', false),
    ('40000000-0000-0000-0000-000000000003', 'c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33', 1, '2026-01-15 08:00:00+00', 'system', false),
    ('40000000-0000-0000-0000-000000000003', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 2, '2026-01-15 08:00:00+00', 'system', false),
    ('40000000-0000-0000-0000-000000000003', '22222222-2222-2222-2222-222222222222', 3, '2026-01-15 08:00:00+00', 'system', false),
    ('40000000-0000-0000-0000-000000000004', '11111111-1111-1111-1111-111111111111', 1, '2026-01-15 08:00:00+00', 'system', false),
    ('40000000-0000-0000-0000-000000000004', '66666666-6666-6666-6666-666666666666', 2, '2026-01-15 08:00:00+00', 'system', false),
    ('40000000-0000-0000-0000-000000000006', '55555555-5555-5555-5555-555555555555', 1, '2026-01-15 08:00:00+00', 'system', false),
    ('40000000-0000-0000-0000-000000000007', 'e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a55', 1, '2026-01-15 08:00:00+00', 'system', false),
    ('40000000-0000-0000-0000-000000000008', '33333333-3333-3333-3333-333333333333', 1, '2026-01-15 08:00:00+00', 'system', false),
    ('40000000-0000-0000-0000-000000000009', '44444444-4444-4444-4444-444444444444', 1, '2026-01-15 08:00:00+00', 'system', false),
    ('40000000-0000-0000-0000-000000000011', 'f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a66', 1, '2026-01-15 08:00:00+00', 'system', false)
ON CONFLICT (unit_id, word_id) DO NOTHING;
