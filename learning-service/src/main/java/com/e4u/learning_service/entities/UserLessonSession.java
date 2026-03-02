package com.e4u.learning_service.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity tracking a user's execution state for a specific lesson.
 * Records progress, accuracy, and completion status.
 * Replaces the old DynamicLesson entity with cleaner separation.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_lesson_sessions", indexes = {
    @Index(name = "idx_session_user", columnList = "user_id"),
    @Index(name = "idx_session_lesson", columnList = "lesson_template_id"),
    @Index(name = "idx_session_status", columnList = "status")
}, uniqueConstraints = {
    @UniqueConstraint(name = "unique_user_lesson_session", columnNames = {"user_id", "lesson_template_id"})
})
public class UserLessonSession extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_template_id", nullable = false)
    private LessonTemplate lessonTemplate;

    /**
     * Reference to the user's unit state (for navigation and progress tracking)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_unit_state_id")
    private UserUnitState userUnitState;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 255)
    @Builder.Default
    private SessionStatus status = SessionStatus.NOT_STARTED;

    @Column(name = "total_items")
    private Integer totalItems;

    @Column(name = "completed_items")
    @Builder.Default
    private Integer completedItems = 0;

    @Column(name = "correct_items")
    @Builder.Default
    private Integer correctItems = 0;

    @Column(name = "accuracy_rate")
    private Float accuracyRate;

    /**
     * Exercise attempts within this session
     */
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    @Builder.Default
    private List<UserExerciseAttempt> exerciseAttempts = new ArrayList<>();

    public enum SessionStatus {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED,
        PAUSED
    }

    /**
     * Start the lesson session
     */
    public void start(int totalExercises) {
        this.status = SessionStatus.IN_PROGRESS;
        this.totalItems = totalExercises;
        this.completedItems = 0;
        this.correctItems = 0;
    }

    /**
     * Record an exercise completion
     */
    public void recordExerciseCompletion(boolean isCorrect) {
        this.completedItems++;
        if (isCorrect) {
            this.correctItems++;
        }
        updateAccuracyRate();
        
        // Check if lesson is completed
        if (this.completedItems >= this.totalItems) {
            this.status = SessionStatus.COMPLETED;
        }
    }

    /**
     * Update accuracy rate based on completed items
     */
    private void updateAccuracyRate() {
        if (completedItems != null && completedItems > 0) {
            this.accuracyRate = (float) correctItems / completedItems * 100;
        } else {
            this.accuracyRate = 0f;
        }
    }

    /**
     * Add an exercise attempt to this session
     */
    public void addExerciseAttempt(UserExerciseAttempt attempt) {
        exerciseAttempts.add(attempt);
        attempt.setSession(this);
    }

    /**
     * Get progress percentage (0-100)
     */
    public float getProgressPercentage() {
        if (totalItems == null || totalItems == 0) return 0f;
        return (float) completedItems / totalItems * 100;
    }

    /**
     * Check if this session is completed
     */
    public boolean isCompleted() {
        return status == SessionStatus.COMPLETED;
    }

    /**
     * Check if this session can be resumed
     */
    public boolean canResume() {
        return status == SessionStatus.IN_PROGRESS || status == SessionStatus.PAUSED;
    }

    /**
     * Pause the session
     */
    public void pause() {
        if (status == SessionStatus.IN_PROGRESS) {
            this.status = SessionStatus.PAUSED;
        }
    }

    /**
     * Resume the session
     */
    public void resume() {
        if (status == SessionStatus.PAUSED) {
            this.status = SessionStatus.IN_PROGRESS;
        }
    }

    /**
     * Complete the session
     */
    public void complete() {
        this.status = SessionStatus.COMPLETED;
        updateAccuracyRate();
    }
}
