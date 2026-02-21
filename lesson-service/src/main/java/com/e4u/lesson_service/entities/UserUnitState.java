package com.e4u.lesson_service.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_unit_state")
public class UserUnitState extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "unit_id", nullable = false)
    private UUID unitId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UnitStatus status;

    @Column(name = "current_priority_score")
    private Integer currentPriorityScore;

    @Column(name = "is_fast_tracked")
    private Boolean isFastTracked;

    @Column(name = "proficiency_score")
    private Float proficiencyScore;

    @Column(name = "difficulty_modifier")
    private Float difficultyModifier;

    @OneToMany(mappedBy = "userUnitState", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<DynamicLesson> lessons;

    @Column(name = "last_interaction_at")
    private LocalDateTime lastInteractionAt;

    public enum UnitStatus {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED
    }
}
