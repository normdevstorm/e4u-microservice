package com.e4u.learning_service.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entity tracking a user's progress state for a specific curriculum unit.
 * Links to lesson sessions for detailed progress tracking.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_unit_state", indexes = {
    @Index(name = "idx_user_unit_state_user", columnList = "user_id"),
    @Index(name = "idx_user_unit_state_unit", columnList = "unit_id")
})
public class UserUnitState extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "unit_id", nullable = false)
    private UUID unitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", insertable = false, updatable = false)
    private CurriculumUnit unit;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private UnitStatus status = UnitStatus.NOT_STARTED;

    @Column(name = "current_priority_score")
    private Integer currentPriorityScore;

    @Column(name = "is_fast_tracked")
    @Builder.Default
    private Boolean isFastTracked = false;

    @Column(name = "proficiency_score")
    private Float proficiencyScore;

    @Column(name = "difficulty_modifier")
    @Builder.Default
    private Float difficultyModifier = 1.0f;

    /**
     * User's lesson sessions within this unit.
     * Replaced old DynamicLesson relationship.
     */
    @OneToMany(mappedBy = "userUnitState", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<UserLessonSession> lessonSessions = new HashSet<>();

    @Column(name = "last_interaction_at")
    private LocalDateTime lastInteractionAt;

    public enum UnitStatus {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED
    }

    /**
     * Add a lesson session to this unit state
     */
    public void addLessonSession(UserLessonSession session) {
        lessonSessions.add(session);
        session.setUserUnitState(this);
    }

    /**
     * Calculate overall unit progress based on lesson sessions
     */
    public float calculateProgress() {
        if (lessonSessions == null || lessonSessions.isEmpty()) {
            return 0f;
        }
        long completedLessons = lessonSessions.stream()
            .filter(UserLessonSession::isCompleted)
            .count();
        return (float) completedLessons / lessonSessions.size() * 100;
    }

    /**
     * Update unit status based on lesson progress
     */
    public void updateStatusFromLessons() {
        if (lessonSessions == null || lessonSessions.isEmpty()) {
            this.status = UnitStatus.NOT_STARTED;
            return;
        }
        
        boolean allCompleted = lessonSessions.stream()
            .allMatch(UserLessonSession::isCompleted);
        boolean anyStarted = lessonSessions.stream()
            .anyMatch(s -> s.getStatus() != UserLessonSession.SessionStatus.NOT_STARTED);
        
        if (allCompleted) {
            this.status = UnitStatus.COMPLETED;
        } else if (anyStarted) {
            this.status = UnitStatus.IN_PROGRESS;
        } else {
            this.status = UnitStatus.NOT_STARTED;
        }
    }
}
