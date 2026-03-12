package com.e4u.learning_service.repositories;

import com.e4u.learning_service.entities.Curriculum;
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
public interface CurriculumRepository extends JpaRepository<Curriculum, UUID>, JpaSpecificationExecutor<Curriculum> {

    // Find by ID (non-deleted)
    Optional<Curriculum> findByIdAndDeletedFalse(UUID id);

    // Find all (non-deleted)
    Page<Curriculum> findByDeletedFalse(Pageable pageable);

    // Find all active curricula (non-deleted)
    List<Curriculum> findByIsActiveTrueAndDeletedFalse();

    // Find by goal ID
    List<Curriculum> findByGoalDefinition_IdAndDeletedFalse(UUID goalId);

    // Find by multiple goal IDs (used for user-specific curriculum fetch)
    List<Curriculum> findByGoalDefinition_IdInAndDeletedFalse(List<UUID> goalIds);

    // Find by name
    Optional<Curriculum> findByCurriculumNameAndDeletedFalse(String curriculumName);

    // Find with units
    @Query("SELECT DISTINCT c FROM Curriculum c LEFT JOIN FETCH c.units u WHERE c.id = :id AND c.deleted = false AND (u IS NULL OR u.deleted = false)")
    Optional<Curriculum> findByIdWithUnits(@Param("id") UUID id);

    // Soft delete
    @Modifying
    @Query("UPDATE Curriculum c SET c.deleted = true, c.deletedAt = :deletedAt WHERE c.id = :id")
    int softDeleteById(@Param("id") UUID id, @Param("deletedAt") Instant deletedAt);

    // Check if exists by name
    boolean existsByCurriculumNameAndDeletedFalse(String curriculumName);
}
