package com.renting.authentication_service.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Permission {

    // ── Own-account operations ──────────────────────────────────────────────
    PROFILE_READ("profile:read"),
    PROFILE_UPDATE("profile:update"),

    // ── Curriculum (CurriculumUnit) ─────────────────────────────────────────
    CURRICULUM_READ("curriculum:read"),
    CURRICULUM_CREATE("curriculum:create"),
    CURRICULUM_UPDATE("curriculum:update"),
    CURRICULUM_DELETE("curriculum:delete"),

    // ── Lesson (LessonTemplate / ExerciseTemplate) ──────────────────────────
    LESSON_READ("lesson:read"),
    LESSON_CREATE("lesson:create"),
    LESSON_UPDATE("lesson:update"),
    LESSON_DELETE("lesson:delete"),

    // ── Vocabulary (GlobalDictionary / VocabAsset / WordContextTemplate) ────
    VOCAB_READ("vocab:read"),
    VOCAB_CREATE("vocab:create"),
    VOCAB_UPDATE("vocab:update"),
    VOCAB_DELETE("vocab:delete"),

    // ── Progress (UserLessonSession / UserExerciseAttempt / UserVocabProgress)
    PROGRESS_READ("progress:read"),
    PROGRESS_WRITE("progress:write"),

    // ── Statistics ──────────────────────────────────────────────────────────
    STATS_READ("stats:read"),
    STATS_WRITE("stats:write"),

    // ── Payment ─────────────────────────────────────────────────────────────
    PAYMENT_READ("payment:read"),
    PAYMENT_CREATE("payment:create"),

    // ── Admin-level user management ─────────────────────────────────────────
    USER_READ("user:read"),
    USER_CREATE("user:create"),
    USER_UPDATE("user:update"),
    USER_DELETE("user:delete"),

    // ── Admin operations ────────────────────────────────────────────────────
    ADMIN_READ("admin:read"),
    ADMIN_CREATE("admin:create"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_DELETE("admin:delete");

    private final String permission;
}
