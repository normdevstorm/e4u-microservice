package com.e4u.learning_service.repositories;

import com.e4u.learning_service.entities.UserLessonSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
