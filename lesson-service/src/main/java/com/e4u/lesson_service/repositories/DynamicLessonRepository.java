package com.e4u.lesson_service.repositories;

import com.e4u.lesson_service.entities.DynamicLesson;
import com.e4u.lesson_service.entities.DynamicLesson.LessonStatus;
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
public interface DynamicLessonRepository
        extends JpaRepository<DynamicLesson, UUID>, JpaSpecificationExecutor<DynamicLesson> {

    // Find by ID (non-deleted)
    Optional<DynamicLesson> findByIdAndDeletedFalse(UUID id);

    // Find all (non-deleted)
    Page<DynamicLesson> findByDeletedFalse(Pageable pageable);

    // Find by User
    List<DynamicLesson> findByUserIdAndDeletedFalse(UUID userId);

    Page<DynamicLesson> findByUserIdAndDeletedFalse(UUID userId, Pageable pageable);

    List<DynamicLesson> findByUserIdAndStatusAndDeletedFalse(UUID userId, LessonStatus status);

    // Find by UserUnitState (Unit)
    List<DynamicLesson> findByUserUnitStateIdAndDeletedFalse(UUID userUnitStateId);

    Page<DynamicLesson> findByUserUnitStateIdAndDeletedFalse(UUID userUnitStateId, Pageable pageable);

    // Find by User and Unit
    List<DynamicLesson> findByUserIdAndUserUnitStateIdAndDeletedFalse(UUID userId, UUID userUnitStateId);

    // Count queries
    long countByUserIdAndDeletedFalse(UUID userId);

    long countByUserIdAndStatusAndDeletedFalse(UUID userId, LessonStatus status);

    // Soft Delete
    @Modifying
    @Query("UPDATE DynamicLesson d SET d.deleted = true, d.deletedAt = :deletedAt WHERE d.id = :id")
    int softDeleteById(@Param("id") UUID id, @Param("deletedAt") Instant deletedAt);

    // Custom query with lesson items
    @Query("SELECT d FROM DynamicLesson d LEFT JOIN FETCH d.lessonItems WHERE d.id = :id AND d.deleted = false")
    Optional<DynamicLesson> findByIdWithLessonItems(@Param("id") UUID id);

    // Check for active lesson
    boolean existsByUserIdAndStatusAndDeletedFalse(UUID userId, LessonStatus status);
}
