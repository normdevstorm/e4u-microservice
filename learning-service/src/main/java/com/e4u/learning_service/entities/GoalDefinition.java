package com.e4u.learning_service.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Entity representing a learning goal definition.
 * Goals define the focus areas and skills for learners.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "goal_definitions")
public class GoalDefinition extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "goal_name", nullable = false, length = 50)
    private String goalName;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "skills_focus", columnDefinition = "TEXT[]")
    @Builder.Default
    private List<String> skillsFocus = new ArrayList<>();

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "goalDefinition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<UserGoal> userGoals = new HashSet<>();

    @OneToMany(mappedBy = "goalDefinition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Curriculum> curricula = new HashSet<>();
}
