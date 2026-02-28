package com.e4u.learning_service.repositories;

import com.e4u.learning_service.entities.UserGoal;
import com.e4u.learning_service.entities.UserGoalId;
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
public interface UserGoalRepository extends JpaRepository<UserGoal, UserGoalId>, JpaSpecificationExecutor<UserGoal> {

    // Find all (non-deleted)
    Page<UserGoal> findByDeletedFalse(Pageable pageable);

    // Find by user ID (non-deleted)
    List<UserGoal> findByUserIdAndDeletedFalse(UUID userId);

    // Find by goal ID (non-deleted)
    List<UserGoal> findByGoalDefinitionIdAndDeletedFalse(UUID goalId);

    // Find specific user-goal combination
    Optional<UserGoal> findByUserIdAndGoalDefinitionIdAndDeletedFalse(UUID userId, UUID goalId);

    // Find primary goal for user
    Optional<UserGoal> findByUserIdAndIsPrimaryTrueAndDeletedFalse(UUID userId);

    // Check if user has a specific goal
    boolean existsByUserIdAndGoalDefinitionIdAndDeletedFalse(UUID userId, UUID goalId);

    // Soft delete by user and goal
    @Modifying
    @Query("UPDATE UserGoal ug SET ug.deleted = true, ug.deletedAt = :deletedAt WHERE ug.userId = :userId AND ug.goalDefinition.id = :goalId")
    int softDeleteByUserIdAndGoalId(@Param("userId") UUID userId, @Param("goalId") UUID goalId,
            @Param("deletedAt") Instant deletedAt);

    // Soft delete all goals for a user
    @Modifying
    @Query("UPDATE UserGoal ug SET ug.deleted = true, ug.deletedAt = :deletedAt WHERE ug.userId = :userId")
    int softDeleteAllByUserId(@Param("userId") UUID userId, @Param("deletedAt") Instant deletedAt);

    // Clear primary flag for user's goals
    @Modifying
    @Query("UPDATE UserGoal ug SET ug.isPrimary = false WHERE ug.userId = :userId AND ug.deleted = false")
    int clearPrimaryForUser(@Param("userId") UUID userId);

    // Find with goal details
    @Query("SELECT ug FROM UserGoal ug JOIN FETCH ug.goalDefinition WHERE ug.userId = :userId AND ug.deleted = false")
    List<UserGoal> findByUserIdWithGoalDetails(@Param("userId") UUID userId);
}
