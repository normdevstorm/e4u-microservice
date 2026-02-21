package com.e4u.curriculum_service.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entity representing a curriculum which is a collection of learning units.
 * Each curriculum is associated with a goal and targets specific learning
 * outcomes.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "curriculum")
public class Curriculum extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "curriculum_name", nullable = false, length = 100)
    private String curriculumName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id")
    private GoalDefinition goalDefinition;

    @Column(name = "target_goals")
    private String targetGoals;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "curriculum", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @OrderBy("defaultOrder ASC")
    private Set<CurriculumUnit> units = new HashSet<>();

    /**
     * Add a unit to this curriculum
     */
    public void addUnit(CurriculumUnit unit) {
        units.add(unit);
        unit.setCurriculum(this);
    }

    /**
     * Remove a unit from this curriculum
     */
    public void removeUnit(CurriculumUnit unit) {
        units.remove(unit);
        unit.setCurriculum(null);
    }
}
