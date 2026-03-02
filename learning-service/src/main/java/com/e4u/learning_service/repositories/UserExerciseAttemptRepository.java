package com.e4u.learning_service.repositories;

import com.e4u.learning_service.entities.ExerciseTemplate;
import com.e4u.learning_service.entities.UserExerciseAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for UserExerciseAttempt entity.
 * Provides access to user's exercise attempt records.
 */
@Repository
public interface UserExerciseAttemptRepository extends JpaRepository<UserExerciseAttempt, UUID> {

       /**
        * Find all attempts within a session
        */
       List<UserExerciseAttempt> findBySessionIdOrderByCreatedAtAsc(UUID sessionId);

       /**
        * Find attempt for a specific exercise within a session
        */
       Optional<UserExerciseAttempt> findBySessionIdAndExerciseTemplateId(UUID sessionId, UUID exerciseTemplateId);

       /**
        * Find attempt with details eagerly loaded
        */
       @Query("SELECT uea FROM UserExerciseAttempt uea " +
                     "LEFT JOIN FETCH uea.exerciseTemplate et " +
                     "LEFT JOIN FETCH et.wordContextTemplate " +
                     "WHERE uea.id = :id")
       Optional<UserExerciseAttempt> findByIdWithDetails(@Param("id") UUID id);

       /**
        * Find all attempts for a user across all sessions
        */
       @Query("SELECT uea FROM UserExerciseAttempt uea " +
                     "JOIN uea.session s " +
                     "WHERE s.userId = :userId " +
                     "ORDER BY uea.createdAt DESC")
       List<UserExerciseAttempt> findByUserId(@Param("userId") UUID userId);

       /**
        * Find correct attempts in a session
        */
       List<UserExerciseAttempt> findBySessionIdAndIsCorrectTrue(UUID sessionId);

       /**
        * Count correct attempts in a session
        */
       long countBySessionIdAndIsCorrectTrue(UUID sessionId);

       /**
        * Count total attempts in a session
        */
       long countBySessionId(UUID sessionId);

       /**
        * Count correct attempts for a session
        */
       @Query("SELECT COUNT(uea) FROM UserExerciseAttempt uea " +
                     "WHERE uea.session.id = :sessionId AND uea.isCorrect = true")
       Long countCorrectBySessionId(@Param("sessionId") UUID sessionId);

       /**
        * Count total attempts for a user
        */
       @Query("SELECT COUNT(uea) FROM UserExerciseAttempt uea " +
                     "JOIN uea.session s WHERE s.userId = :userId")
       Long countByUserId(@Param("userId") UUID userId);

       /**
        * Count correct attempts for a user
        */
       @Query("SELECT COUNT(uea) FROM UserExerciseAttempt uea " +
                     "JOIN uea.session s WHERE s.userId = :userId AND uea.isCorrect = true")
       Long countCorrectByUserId(@Param("userId") UUID userId);

       /**
        * Find recent attempts for a user with limit
        */
       @Query("SELECT uea FROM UserExerciseAttempt uea " +
                     "JOIN uea.session s " +
                     "WHERE s.userId = :userId " +
                     "ORDER BY uea.createdAt DESC")
       List<UserExerciseAttempt> findRecentByUserId(@Param("userId") UUID userId,
                     org.springframework.data.domain.Pageable pageable);

       /**
        * Find attempts for a specific exercise type
        */
       @Query("SELECT uea FROM UserExerciseAttempt uea " +
                     "JOIN uea.exerciseTemplate et " +
                     "JOIN uea.session s " +
                     "WHERE s.userId = :userId " +
                     "AND et.exerciseType = :exerciseType")
       List<UserExerciseAttempt> findByUserIdAndExerciseType(
                     @Param("userId") UUID userId,
                     @Param("exerciseType") ExerciseTemplate.ExerciseType exerciseType);

       /**
        * Calculate accuracy rate for a specific exercise type
        */
       @Query("SELECT CAST(SUM(CASE WHEN uea.isCorrect = true THEN 1 ELSE 0 END) AS float) / COUNT(uea) * 100 " +
                     "FROM UserExerciseAttempt uea " +
                     "JOIN uea.exerciseTemplate et " +
                     "JOIN uea.session s " +
                     "WHERE s.userId = :userId " +
                     "AND et.exerciseType = :exerciseType")
       Optional<Float> findAccuracyByExerciseType(
                     @Param("userId") UUID userId,
                     @Param("exerciseType") ExerciseTemplate.ExerciseType exerciseType);

       /**
        * Find attempts targeting a specific word context template
        */
       @Query("SELECT uea FROM UserExerciseAttempt uea " +
                     "JOIN uea.exerciseTemplate et " +
                     "JOIN uea.session s " +
                     "WHERE s.userId = :userId " +
                     "AND et.wordContextTemplate.id = :wordContextTemplateId " +
                     "ORDER BY uea.createdAt DESC")
       List<UserExerciseAttempt> findByUserIdAndWordContextTemplateId(
                     @Param("userId") UUID userId,
                     @Param("wordContextTemplateId") UUID wordContextTemplateId);

       /**
        * Check if attempt exists in session
        */
       boolean existsBySessionIdAndExerciseTemplateId(UUID sessionId, UUID exerciseTemplateId);

       /**
        * Delete all attempts in a session
        */
       void deleteBySessionId(UUID sessionId);
}
