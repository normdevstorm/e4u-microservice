package com.e4u.learning_service.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * Composite primary key for UserGoal entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserGoalId implements Serializable {

    private UUID userId;
    private UUID goalId;
}
