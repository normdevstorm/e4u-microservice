package com.e4u.ai_filter_service.learning.repository;

import com.e4u.ai_filter_service.learning.entity.UserGoalReadOnly;
import com.e4u.ai_filter_service.learning.entity.UserGoalReadOnlyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Read-only repository for {@link UserGoalReadOnly} on {@code e4u_learning}.
 *
 * <p>
 * Backed by {@code learningEntityManager}. Used by the batch processor to
 * resolve the list of active goal names for a given user, which is included
 * in the AI relevance request.
 */
@Repository
@Transactional(readOnly = true, transactionManager = "learningTransactionManager")
public interface UserGoalReadRepository extends JpaRepository<UserGoalReadOnly, UserGoalReadOnlyId> {

    /**
     * Find all goal records for a user.
     * The goal name is accessible via {@code userGoal.getGoalName()} through the
     * eagerly-loaded {@code GoalDefinitionReadOnly} association.
     */
    @Query("SELECT ug FROM UserGoalReadOnly ug WHERE ug.userId = :userId")
    List<UserGoalReadOnly> findByUserId(@Param("userId") UUID userId);
}
