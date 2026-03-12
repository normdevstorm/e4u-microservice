--liquibase formatted sql
--changeset dev:012-baseline-questions comment:Create baseline_questions table and seed 25 placement questions

CREATE TABLE IF NOT EXISTS public.baseline_questions (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    cefr_tier       TEXT        NOT NULL CHECK (cefr_tier IN ('A1','A2','B1','B2','C1')),
    prompt          TEXT        NOT NULL,
    options         TEXT[]      NOT NULL,
    correct_answer  TEXT        NOT NULL,
    sort_order      INT         NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_baseline_questions_cefr ON public.baseline_questions(cefr_tier);

-- ── A1 questions (basic grammar / vocabulary) ─────────────────────────────────
INSERT INTO public.baseline_questions (cefr_tier, prompt, options, correct_answer, sort_order) VALUES
('A1', 'She ___ to school every day.',
 ARRAY['go','goes','going','gone'], 'goes', 1),

('A1', 'I have ___ umbrella in my bag.',
 ARRAY['a','an','the','—'], 'an', 2),

('A1', 'What is the plural of "child"?',
 ARRAY['childs','children','childes','childrens'], 'children', 3),

('A1', '___ your name Maria?',
 ARRAY['Am','Is','Are','Be'], 'Is', 4),

('A1', 'He ___ not like coffee.',
 ARRAY['do','does','is','has'], 'does', 5),

-- ── A2 questions ─────────────────────────────────────────────────────────────
('A2', 'When I arrived, she ___ already left.',
 ARRAY['has','have','had','was'], 'had', 6),

('A2', 'I ___ living in Paris for three years now.',
 ARRAY['am','was','have been','had been'], 'have been', 7),

('A2', 'Choose the correct sentence.',
 ARRAY['She speak English well.','She speaks English well.','She is speak English well.','She speaking English well.'],
 'She speaks English well.', 8),

('A2', 'He is ___ student in the class.',
 ARRAY['tallest','the tallest','more tall','most tall'], 'the tallest', 9),

('A2', 'We ___ finish this report by tomorrow.',
 ARRAY['should','would','might','must'], 'must', 10),

-- ── B1 questions ─────────────────────────────────────────────────────────────
('B1', 'The meeting was postponed ___ the manager was ill.',
 ARRAY['because','although','so that','in spite of'], 'because', 11),

('B1', 'If I ___ more time, I would travel the world.',
 ARRAY['have','had','will have','would have'], 'had', 12),

('B1', 'The report ___ by the team last Friday.',
 ARRAY['completed','was completed','has completed','is completing'], 'was completed', 13),

('B1', 'She ___ to the gym three times a week since January.',
 ARRAY['goes','went','has been going','had gone'], 'has been going', 14),

('B1', 'Despite ___ hard, he failed the exam.',
 ARRAY['studying','study','studied','to study'], 'studying', 15),

-- ── B2 questions ─────────────────────────────────────────────────────────────
('B2', 'Hardly ___ he arrived when the problems started.',
 ARRAY['had','did','was','has'], 'had', 16),

('B2', 'The scientist to ___ the award was given has published many papers.',
 ARRAY['who','whom','which','whose'], 'whom', 17),

('B2', 'By the time you read this letter, I ___ the country.',
 ARRAY['will leave','will have left','am leaving','have left'], 'will have left', 18),

('B2', '___ the heavy traffic, we managed to arrive on time.',
 ARRAY['Despite','Although','Because','However'], 'Despite', 19),

('B2', 'She ___ rather stay home than go to the party.',
 ARRAY['will','would','should','might'], 'would', 20),

-- ── C1 questions ─────────────────────────────────────────────────────────────
('C1', 'The findings of the study ___ further research into this phenomenon.',
 ARRAY['warrant','warrants','warranting','warranted'], 'warrant', 21),

('C1', 'Not until she read the letter ___ the truth.',
 ARRAY['she knew','she did know','did she know','knew she'], 'did she know', 22),

('C1', 'The proposal was met with considerable ___ from the board.',
 ARRAY['scepticism','sceptical','sceptically','scepticise'], 'scepticism', 23),

('C1', 'It is imperative that every delegate ___ the pre-meeting briefing.',
 ARRAY['attends','attend','attended','to attend'], 'attend', 24),

('C1', 'The legislation was ___ amended to include additional safeguards.',
 ARRAY['subsequently','subsequential','subsequence','subsequenced'], 'subsequently', 25);

-- rollback DROP TABLE IF EXISTS baseline_questions;
