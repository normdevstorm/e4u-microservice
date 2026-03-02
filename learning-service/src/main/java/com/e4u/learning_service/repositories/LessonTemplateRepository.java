package com.e4u.learning_service.repositories;

import com.e4u.learning_service.entities.LessonTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for LessonTemplate entity.
 * Provides access to static lesson definitions within curriculum units.
 */
@Repository
public interface LessonTemplateRepository extends JpaRepository<LessonTemplate, UUID> {

    /**
     * Find all lessons for a unit, ordered by sequence
     */
    List<LessonTemplate> findByUnitIdOrderBySequenceOrderAsc(UUID unitId);

    /**
     * Find lesson by unit and sequence order
     */
    Optional<LessonTemplate> findByUnitIdAndSequenceOrder(UUID unitId, Integer sequenceOrder);

    /**
     * Find lessons by type within a unit
     */
    List<LessonTemplate> findByUnitIdAndLessonType(UUID unitId, LessonTemplate.LessonType lessonType);

    /**
     * Find lesson with exercise templates eagerly loaded
     */
    @Query("SELECT lt FROM LessonTemplate lt " +
           "LEFT JOIN FETCH lt.exerciseTemplates " +
           "WHERE lt.id = :id")
    Optional<LessonTemplate> findByIdWithExercises(@Param("id") UUID id);

    /**
     * Find lesson with unit eagerly loaded
     */
    @Query("SELECT lt FROM LessonTemplate lt " +
           "LEFT JOIN FETCH lt.unit " +
           "WHERE lt.id = :id")
    Optional<LessonTemplate> findByIdWithUnit(@Param("id") UUID id);

    /**
     * Find all lessons for a unit with exercises (for session generation)
     */
    @Query("SELECT DISTINCT lt FROM LessonTemplate lt " +
           "LEFT JOIN FETCH lt.exerciseTemplates et " +
           "WHERE lt.unit.id = :unitId " +
           "AND (et.createdForUserId IS NULL OR et.createdForUserId = :userId) " +
           "ORDER BY lt.sequenceOrder ASC")
    List<LessonTemplate> findByUnitIdWithExercisesForUser(
        @Param("unitId") UUID unitId, 
        @Param("userId") UUID userId
    );

    /**
     * Count lessons in a unit
     */
    long countByUnitId(UUID unitId);

    /**
     * Find next lesson in sequence
     */
    @Query("SELECT lt FROM LessonTemplate lt " +
           "WHERE lt.unit.id = :unitId " +
           "AND lt.sequenceOrder > :currentOrder " +
           "ORDER BY lt.sequenceOrder ASC " +
           "LIMIT 1")
    Optional<LessonTemplate> findNextLesson(
        @Param("unitId") UUID unitId, 
        @Param("currentOrder") Integer currentOrder
    );

    /**
     * Check if lesson exists in unit
     */
    boolean existsByUnitIdAndLessonName(UUID unitId, String lessonName);
}
