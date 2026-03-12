--liquibase formatted sql
--changeset dev:011-onboarding-reference-data comment:Create onboarding reference tables and seed data

CREATE TABLE IF NOT EXISTS public.onboarding_occupations (
    id          UUID    PRIMARY KEY DEFAULT gen_random_uuid(),
    code        TEXT    NOT NULL UNIQUE,
    label       TEXT    NOT NULL,
    sort_order  INT     NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS public.onboarding_interests (
    id          UUID    PRIMARY KEY DEFAULT gen_random_uuid(),
    code        TEXT    NOT NULL UNIQUE,
    label       TEXT    NOT NULL,
    sort_order  INT     NOT NULL DEFAULT 0
);

-- ── Occupation seed data ──────────────────────────────────────────────────────

INSERT INTO public.onboarding_occupations (code, label, sort_order) VALUES
    ('student',          'Student',               1),
    ('professional',     'Professional',          2),
    ('freelancer',       'Freelancer',            3),
    ('researcher',       'Researcher',            4),
    ('teacher_educator', 'Teacher / Educator',    5),
    ('business_owner',   'Business Owner',        6),
    ('job_seeker',       'Job Seeker',            7),
    ('retired',          'Retired',               8),
    ('other',            'Other',                 9)
ON CONFLICT (code) DO NOTHING;

-- ── Interest seed data ────────────────────────────────────────────────────────

INSERT INTO public.onboarding_interests (code, label, sort_order) VALUES
    ('business',         'Business',              1),
    ('travel',           'Travel',                2),
    ('technology',       'Technology',            3),
    ('science',          'Science',               4),
    ('culture_arts',     'Culture & Arts',        5),
    ('sports',           'Sports',                6),
    ('education',        'Education',             7),
    ('health_wellness',  'Health & Wellness',     8),
    ('food_cuisine',     'Food & Cuisine',        9),
    ('news_current',     'News & Current Affairs',10),
    ('entertainment',    'Entertainment',         11),
    ('environment',      'Environment',           12);

-- rollback DROP TABLE IF EXISTS onboarding_interests; DROP TABLE IF EXISTS onboarding_occupations;
