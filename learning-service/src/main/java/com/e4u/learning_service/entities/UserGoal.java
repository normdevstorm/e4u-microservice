package com.e4u.learning_service.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Entity representing the relationship between users and their learning goals.
 * Users can have multiple goals with one marked as primary.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_goals")
@IdClass(UserGoalId.class)
public class UserGoal extends BaseEntity {

    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Id
    @Column(name = "goal_id", nullable = false)
    private UUID goalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false, insertable = false, updatable = false)
    private GoalDefinition goalDefinition;

    @Column(name = "is_primary", nullable = false)
    @Builder.Default
    private Boolean isPrimary = false;

    @Column(name = "started_at")
    @Builder.Default
    private Instant startedAt = Instant.now();

    /**
     * Helper method to set goal definition and sync goal_id
     */
    public void setGoalDefinition(GoalDefinition goalDefinition) {
        this.goalDefinition = goalDefinition;
        if (goalDefinition != null) {
            this.goalId = goalDefinition.getId();
        }
    }
}
