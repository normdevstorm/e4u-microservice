package com.e4u.curriculum_service.repositories;

import com.e4u.curriculum_service.entities.GoalDefinition;
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
public interface GoalDefinitionRepository
        extends JpaRepository<GoalDefinition, UUID>, JpaSpecificationExecutor<GoalDefinition> {

    // Find by ID (non-deleted)
    Optional<GoalDefinition> findByIdAndDeletedFalse(UUID id);

    // Find all (non-deleted)
    Page<GoalDefinition> findByDeletedFalse(Pageable pageable);

    // Find all active goals (non-deleted)
    List<GoalDefinition> findByIsActiveTrueAndDeletedFalse();

    // Find by name
    Optional<GoalDefinition> findByGoalNameAndDeletedFalse(String goalName);

    // Soft delete
    @Modifying
    @Query("UPDATE GoalDefinition g SET g.deleted = true, g.deletedAt = :deletedAt WHERE g.id = :id")
    int softDeleteById(@Param("id") UUID id, @Param("deletedAt") Instant deletedAt);

    // Check if exists by name
    boolean existsByGoalNameAndDeletedFalse(String goalName);
}
