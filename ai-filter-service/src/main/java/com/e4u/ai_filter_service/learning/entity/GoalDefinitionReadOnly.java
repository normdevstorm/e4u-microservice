package com.e4u.ai_filter_service.learning.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.util.UUID;

/**
 * Minimal read-only projection of {@code goal_definitions} in
 * {@code e4u_learning}.
 *
 * <p>
 * Only {@code id} and {@code goal_name} are needed by the ai-filter-service.
 * Navigated from {@link UserGoalReadOnly#getGoalDefinition()}.
 */
@Entity
@Immutable
@Getter
@NoArgsConstructor
@Table(name = "goal_definitions")
public class GoalDefinitionReadOnly {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "goal_name", updatable = false, nullable = false, length = 50)
    private String goalName;
}
