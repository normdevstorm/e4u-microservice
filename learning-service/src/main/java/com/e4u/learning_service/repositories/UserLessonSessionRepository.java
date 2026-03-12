package com.e4u.learning_service.repositories;

import com.e4u.learning_service.entities.UserLessonSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for UserLessonSession entity.
 * Provides access to user's lesson execution state and progress.
 */
@Repository
public interface UserLessonSessionRepository extends JpaRepository<UserLessonSession, UUID> {

       /**
        * Find all sessions for a specific user and lesson template, most-recent first.
        * Multiple sessions per user/lesson are allowed (e.g., re-attempts after
        * completion).
        */
       @Query("SELECT uls FROM UserLessonSession uls " +
                     "WHERE uls.userId = :userId AND uls.lessonTemplate.id = :lessonTemplateId " +
                     "ORDER BY uls.createdAt DESC")
       List<UserLessonSession> findAllByUserIdAndLessonTemplateId(
                     @Param("userId") UUID userId,
                     @Param("lessonTemplateId") UUID lessonTemplateId);

       /**
        * Find all sessions for a user
        */
       List<UserLessonSession> findByUserId(UUID userId);

       /**
        * Find all sessions for a user within a unit state
        */
       List<UserLessonSession> findByUserUnitStateId(UUID userUnitStateId);

       /**
        * Find sessions by status
        */
       List<UserLessonSession> findByUserIdAndStatus(UUID userId, UserLessonSession.SessionStatus status);

       /**
        * Find in-progress or paused sessions (resumable)
        */
       @Query("SELECT uls FROM UserLessonSession uls " +
                     "WHERE uls.userId = :userId " +
                     "AND uls.status IN ('IN_PROGRESS', 'PAUSED') " +
                     "ORDER BY uls.updatedAt DESC")
       List<UserLessonSession> findResumableSessions(@Param("userId") UUID userId);

       /**
        * Find session with lesson template eagerly loaded
        */
       @Query("SELECT uls FROM UserLessonSession uls " +
                     "LEFT JOIN FETCH uls.lessonTemplate " +
                     "WHERE uls.id = :id")
       Optional<UserLessonSession> findByIdWithLessonTemplate(@Param("id") UUID id);

       /**
        * Find session with all details (lesson, unit, attempts)
        */
       @Query("SELECT uls FROM UserLessonSession uls " +
                     "LEFT JOIN FETCH uls.lessonTemplate lt " +
                     "LEFT JOIN FETCH lt.unit " +
                     "LEFT JOIN FETCH uls.exerciseAttempts " +
                     "WHERE uls.id = :id")
       Optional<UserLessonSession> findByIdWithDetails(@Param("id") UUID id);

       /**
        * Find completed sessions for a user
        */
       List<UserLessonSession> findByUserIdAndStatusOrderByUpdatedAtDesc(
                     UUID userId,
                     UserLessonSession.SessionStatus status);

       /**
        * Count completed sessions for a user
        */
       long countByUserIdAndStatus(UUID userId, UserLessonSession.SessionStatus status);

       /**
        * Calculate average accuracy for a user
        */
       @Query("SELECT AVG(uls.accuracyRate) FROM UserLessonSession uls " +
                     "WHERE uls.userId = :userId AND uls.status = 'COMPLETED'")
       Optional<Float> findAverageAccuracyByUserId(@Param("userId") UUID userId);

       /**
        * Check if session exists for user and lesson
        */
       boolean existsByUserIdAndLessonTemplateId(UUID userId, UUID lessonTemplateId);

       /**
        * Find sessions for lessons in a specific unit
        */
       @Query("SELECT uls FROM UserLessonSession uls " +
                     "JOIN uls.lessonTemplate lt " +
                     "WHERE uls.userId = :userId " +
                     "AND lt.unit.id = :unitId " +
                     "ORDER BY lt.sequenceOrder ASC")
       List<UserLessonSession> findByUserIdAndUnitId(
                     @Param("userId") UUID userId,
                     @Param("unitId") UUID unitId);

       /**
        * Delete all sessions for a user
        */
       void deleteByUserId(UUID userId);

       // ── Stats Queries ───────────────────────────────────────────────────────

       /**
        * Find paginated COMPLETED sessions for a user (session history).
        * Eagerly fetches lessonTemplate for unitTitle without N+1 queries.
        */
       @Query("SELECT uls FROM UserLessonSession uls " +
                     "LEFT JOIN FETCH uls.lessonTemplate lt " +
                     "WHERE uls.userId = :userId  " +
                     // "AND uls.status = 'COMPLETED' " +
                     "ORDER BY uls.createdAt DESC")
       Page<UserLessonSession> findCompletedSessionsByUserId(
                     @Param("userId") UUID userId, Pageable pageable);

       /**
        * Count query companion for the paginated sessions (required when using FETCH
        * JOIN with Page).
        */
       @Query("SELECT COUNT(uls) FROM UserLessonSession uls " +
                     "WHERE uls.userId = :userId AND uls.status = 'COMPLETED'")
       long countCompletedSessionsByUserId(@Param("userId") UUID userId);

       /**
        * Return one distinct calendar date per completed session day (UTC),
        * descending.
        * Used for streak calculation — each row is one study day.
        */
       @Query(value = "SELECT DISTINCT CAST(s.created_at AT TIME ZONE 'UTC' AS DATE) AS study_date " +
                     "FROM user_lesson_sessions s " +
                     "WHERE s.user_id = :userId AND s.status = 'COMPLETED' " +
                     "AND s.deleted = false " +
                     "ORDER BY study_date DESC", nativeQuery = true)
       List<java.sql.Date> findDistinctStudyDates(@Param("userId") UUID userId);

       /**
        * Aggregate completed sessions by calendar day (UTC) within a time window.
        * Result columns per row (Object[]): activity_date, sessions_count,
        * words_learned, total_items_sum, avg_accuracy, study_time_seconds
        *
        * <p>
        * words_learned (index 2) = COUNT(DISTINCT user_vocab_progress.id) WHERE
        * created_at falls on that calendar day — i.e., vocabulary words newly added to
        * the user's SRS deck on that day. This is semantically correct: a new entry in
        * user_vocab_progress means the user encountered and began learning a new word.
        *
        * <p>
        * NOTE: correct_items / total_items from user_lesson_sessions are
        * exercise-level
        * counts (each word generates multiple exercises), so they must NOT be used as
        * a
        * proxy for "words learned".
        *
        * Used to build the 7-bar activity chart on the FE.
        */
       @Query(value = "SELECT " +
                     "  CAST(s.created_at AT TIME ZONE 'UTC' AS DATE)              AS activity_date, " +
                     "  COUNT(DISTINCT s.id)                                        AS sessions_count, " +
                     "  COALESCE(MAX(dv.words_learned), 0)                         AS words_learned, " +
                     "  SUM(COALESCE(s.total_items, 0))                            AS total_items_sum, " +
                     "  COALESCE(AVG(s.accuracy_rate), 0)                          AS avg_accuracy, " +
                     "  COALESCE(SUM(a.time_taken_seconds), 0)                     AS study_time_seconds " +
                     "FROM user_lesson_sessions s " +
                     "LEFT JOIN user_exercise_attempts a ON a.session_id = s.id AND a.deleted = false " +
                     "LEFT JOIN ( " +
                     "  SELECT " +
                     "    CAST(uvp.created_at AT TIME ZONE 'UTC' AS DATE)           AS vp_date, " +
                     "    COUNT(DISTINCT uvp.id)                                    AS words_learned " +
                     "  FROM user_vocab_progress uvp " +
                     "  WHERE uvp.user_id = :userId " +
                     "    AND uvp.deleted = false " +
                     "    AND uvp.created_at >= :from AND uvp.created_at < :to " +
                     "  GROUP BY CAST(uvp.created_at AT TIME ZONE 'UTC' AS DATE) " +
                     ") dv ON dv.vp_date = CAST(s.created_at AT TIME ZONE 'UTC' AS DATE) " +
                     "WHERE s.user_id = :userId " +
                     "  AND s.status = 'COMPLETED' " +
                     "  AND s.deleted = false " +
                     "  AND s.created_at >= :from AND s.created_at < :to " +
                     "GROUP BY CAST(s.created_at AT TIME ZONE 'UTC' AS DATE) " +
                     "ORDER BY activity_date ASC", nativeQuery = true)
       List<Object[]> findWeeklyActivityRaw(
                     @Param("userId") UUID userId,
                     @Param("from") Instant from,
                     @Param("to") Instant to);

       /**
        * Sum total study time in seconds for a user across all completed sessions.
        * Derived from exercise attempts linked to COMPLETED sessions.
        */
       @Query("SELECT COALESCE(SUM(a.timeTakenSeconds), 0) " +
                     "FROM UserExerciseAttempt a " +
                     "WHERE a.session.userId = :userId "
       // +
       // "AND a.session.status = 'COMPLETED'"
       )
       Long sumTotalStudyTimeSecondsByUserId(@Param("userId") UUID userId);
}
