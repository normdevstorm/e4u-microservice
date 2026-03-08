package com.e4u.ai_filter_service.learning.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.util.UUID;

/**
 * Read-only JPA projection of {@code user_goals} joined to
 * {@code goal_definitions}
 * in {@code e4u_learning}.
 *
 * <p>
 * Managed by {@code learningEntityManager}. Used exclusively by the batch
 * processor to fetch a user's active goal names before building the AI
 * relevance
 * request.
 *
 * <p>
 * The join to {@code goal_definitions} is expressed as a {@link ManyToOne}
 * association
 * on the {@code goal_id} FK so that the goal name is accessible via
 * {@code userGoal.getGoalName()} without an extra repository call.
 */
@Entity
@Immutable
@Getter
@NoArgsConstructor
@Table(name = "user_goals")
public class UserGoalReadOnly {

    /**
     * Composite PK — replicates the {@code @IdClass(UserGoalId)} pattern.
     * Here we map only {@code user_id} as the "primary" column and use
     * {@code goal_id} as a regular column since we only read, never write.
     */
    @EmbeddedId
    private UserGoalReadOnlyId id;

    /** UUID of the learner. Convenience accessor backed by the embedded PK. */
    @Column(name = "user_id", insertable = false, updatable = false)
    private UUID userId;

    /** UUID FK to {@code goal_definitions.id}. */
    @Column(name = "goal_id", insertable = false, updatable = false)
    private UUID goalId;

    /** Whether this goal is the user's primary goal. */
    @Column(name = "is_primary", updatable = false)
    private Boolean isPrimary;

    /**
     * Joined goal name — resolved from {@code goal_definitions.goal_name}.
     * Eagerly loaded; used directly to populate the AI request.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "goal_id", referencedColumnName = "id", insertable = false, updatable = false)
    private GoalDefinitionReadOnly goalDefinition;

    // ─── Convenience ────────────────────────────────────────────────────────

    /**
     * Returns the goal name string, or {@code null} if the definition is absent.
     */
    public String getGoalName() {
        return goalDefinition != null ? goalDefinition.getGoalName() : null;
    }
}
