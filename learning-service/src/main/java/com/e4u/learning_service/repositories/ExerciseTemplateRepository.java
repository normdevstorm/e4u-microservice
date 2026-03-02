package com.e4u.learning_service.repositories;

import com.e4u.learning_service.entities.ExerciseTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for ExerciseTemplate entity.
 * Provides access to exercise templates - both shared and user-specific.
 */
@Repository
public interface ExerciseTemplateRepository extends JpaRepository<ExerciseTemplate, UUID> {

    /**
     * Find all exercises for a lesson (shared only)
     */
    List<ExerciseTemplate> findByLessonTemplateIdAndCreatedForUserIdIsNull(UUID lessonTemplateId);

    /**
     * Find all exercises for a lesson available to a user
     */
    @Query("SELECT et FROM ExerciseTemplate et " +
            "WHERE et.lessonTemplate.id = :lessonTemplateId " +
            "AND (et.createdForUserId IS NULL OR et.createdForUserId = :userId)")
    List<ExerciseTemplate> findByLessonTemplateIdForUser(
            @Param("lessonTemplateId") UUID lessonTemplateId,
            @Param("userId") UUID userId);

    /**
     * Find exercises by type within a lesson
     */
    @Query("SELECT et FROM ExerciseTemplate et " +
            "WHERE et.lessonTemplate.id = :lessonTemplateId " +
            "AND et.exerciseType = :exerciseType " +
            "AND (et.createdForUserId IS NULL OR et.createdForUserId = :userId)")
    List<ExerciseTemplate> findByLessonAndTypeForUser(
            @Param("lessonTemplateId") UUID lessonTemplateId,
            @Param("exerciseType") ExerciseTemplate.ExerciseType exerciseType,
            @Param("userId") UUID userId);

    /**
     * Find exercises targeting a specific word context template
     */
    List<ExerciseTemplate> findByWordContextTemplateId(UUID wordContextTemplateId);

    /**
     * Find exercises targeting a word context template for a specific user
     */
    @Query("SELECT et FROM ExerciseTemplate et " +
            "WHERE et.wordContextTemplate.id = :wordContextTemplateId " +
            "AND (et.createdForUserId IS NULL OR et.createdForUserId = :userId)")
    List<ExerciseTemplate> findByWordContextTemplateIdForUser(
            @Param("wordContextTemplateId") UUID wordContextTemplateId,
            @Param("userId") UUID userId);

    /**
     * Find exercise with lesson template eagerly loaded
     */
    @Query("SELECT et FROM ExerciseTemplate et " +
            "LEFT JOIN FETCH et.lessonTemplate " +
            "LEFT JOIN FETCH et.wordContextTemplate " +
            "WHERE et.id = :id")
    Optional<ExerciseTemplate> findByIdWithDetails(@Param("id") UUID id);

    /**
     * Find all user-specific exercises created for a user
     */
    List<ExerciseTemplate> findByCreatedForUserId(UUID userId);

    /**
     * Count exercises in a lesson
     */
    @Query("SELECT COUNT(et) FROM ExerciseTemplate et " +
            "WHERE et.lessonTemplate.id = :lessonTemplateId " +
            "AND (et.createdForUserId IS NULL OR et.createdForUserId = :userId)")
    long countByLessonTemplateIdForUser(
            @Param("lessonTemplateId") UUID lessonTemplateId,
            @Param("userId") UUID userId);

    /**
     * Check if exercise exists for lesson and word context template
     */
    @Query("SELECT CASE WHEN COUNT(et) > 0 THEN true ELSE false END FROM ExerciseTemplate et " +
            "WHERE et.lessonTemplate.id = :lessonTemplateId " +
            "AND et.wordContextTemplate.id = :wordContextTemplateId " +
            "AND et.exerciseType = :exerciseType")
    boolean existsByLessonAndWordContextTemplateAndType(
            @Param("lessonTemplateId") UUID lessonTemplateId,
            @Param("wordContextTemplateId") UUID wordContextTemplateId,
            @Param("exerciseType") ExerciseTemplate.ExerciseType exerciseType);

    /**
     * Delete user-specific exercises for a user
     */
    void deleteByCreatedForUserId(UUID userId);
}
