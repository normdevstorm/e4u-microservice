package com.e4u.ai_filter_service.learning.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * Composite primary key for {@link UserGoalReadOnly}, mirroring the
 * {@code user_goals} composite PK {@code (user_id, goal_id)}.
 */
@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserGoalReadOnlyId implements Serializable {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "goal_id", nullable = false)
    private UUID goalId;
}
