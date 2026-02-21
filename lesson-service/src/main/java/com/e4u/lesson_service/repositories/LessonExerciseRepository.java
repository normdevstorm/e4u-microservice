package com.e4u.lesson_service.repositories;

import com.e4u.lesson_service.entities.LessonExercise;
import com.e4u.lesson_service.entities.LessonExercise.ExerciseType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LessonExerciseRepository
        extends JpaRepository<LessonExercise, UUID>, JpaSpecificationExecutor<LessonExercise> {

    // Find by ID (non-deleted)
    Optional<LessonExercise> findByIdAndDeletedFalse(UUID id);

    // Find all (non-deleted)
    Page<LessonExercise> findByDeletedFalse(Pageable pageable);

    // Find by exercise type
    List<LessonExercise> findByExerciseTypeAndDeletedFalse(ExerciseType exerciseType);

    Page<LessonExercise> findByExerciseTypeAndDeletedFalse(ExerciseType exerciseType, Pageable pageable);

    // Find by vocab instance (word instance)
    List<LessonExercise> findByWordInstanceIdAndDeletedFalse(UUID wordInstanceId);

    Page<LessonExercise> findByWordInstanceIdAndDeletedFalse(UUID wordInstanceId, Pageable pageable);

    // Find by lesson
    List<LessonExercise> findByLessonIdAndDeletedFalse(UUID lessonId);

    Page<LessonExercise> findByLessonIdAndDeletedFalse(UUID lessonId, Pageable pageable);

    // Find by lesson ordered by sequence
    List<LessonExercise> findByLessonIdAndDeletedFalseOrderBySequenceOrderAsc(UUID lessonId);

    // Find by unit (via lesson -> userUnitState)
    @Query("SELECT e FROM LessonExercise e " +
            "JOIN e.lesson l " +
            "JOIN l.userUnitState u " +
            "WHERE u.id = :unitId AND e.deleted = false " +
            "ORDER BY e.sequenceOrder")
    List<LessonExercise> findByUnitId(@Param("unitId") UUID unitId);

    @Query("SELECT e FROM LessonExercise e " +
            "JOIN e.lesson l " +
            "JOIN l.userUnitState u " +
            "WHERE u.id = :unitId AND e.deleted = false")
    Page<LessonExercise> findByUnitId(@Param("unitId") UUID unitId, Pageable pageable);

    // Find by lesson and exercise type
    List<LessonExercise> findByLessonIdAndExerciseTypeAndDeletedFalse(UUID lessonId, ExerciseType exerciseType);

    // Count queries
    long countByLessonIdAndDeletedFalse(UUID lessonId);

    long countByLessonIdAndIsCompletedAndDeletedFalse(UUID lessonId, Boolean isCompleted);

    long countByWordInstanceIdAndDeletedFalse(UUID wordInstanceId);

    // Soft delete
    @Modifying
    @Query("UPDATE LessonExercise e SET e.deleted = true, e.deletedAt = :deletedAt WHERE e.id = :id")
    int softDeleteById(@Param("id") UUID id, @Param("deletedAt") Instant deletedAt);

    @Modifying
    @Query("UPDATE LessonExercise e SET e.deleted = true, e.deletedAt = :deletedAt WHERE e.id IN :ids")
    int softDeleteByIds(@Param("ids") List<UUID> ids, @Param("deletedAt") Instant deletedAt);

    // Check existence
    boolean existsByLessonIdAndWordInstanceIdAndExerciseTypeAndDeletedFalse(
            UUID lessonId, UUID wordInstanceId, ExerciseType exerciseType);
}
