package com.e4u.learning_service.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "dynamic_lesson")
public class DynamicLesson extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_unit_state_id")
    private UserUnitState userUnitState;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private LessonStatus status = LessonStatus.NOT_STARTED;

    @Column(name = "accuracy_rate")
    private Float accuracyRate;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "total_items")
    @Builder.Default
    private Integer totalItems = 0;

    @Column(name = "completed_items")
    @Builder.Default
    private Integer completedItems = 0;

    @Column(name = "correct_items")
    @Builder.Default
    private Integer correctItems = 0;

    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<LessonItem> lessonItems = new HashSet<>();

    public enum LessonStatus {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED
    }

    public void addLessonItem(LessonItem item) {
        lessonItems.add(item);
        item.setLesson(this);
        this.totalItems = lessonItems.size();
    }

    public void removeLessonItem(LessonItem item) {
        lessonItems.remove(item);
        item.setLesson(null);
        this.totalItems = lessonItems.size();
    }

    public void startLesson() {
        this.status = LessonStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
        this.lastActivityAt = LocalDateTime.now();
    }

    public void completeLesson(Float accuracyRate) {
        this.status = LessonStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.lastActivityAt = LocalDateTime.now();
        this.accuracyRate = accuracyRate;
    }

    /**
     * Update last activity timestamp and recalculate accuracy
     */
    public void recordActivity() {
        this.lastActivityAt = LocalDateTime.now();
    }

    /**
     * Increment completed items and optionally correct items
     */
    public void incrementProgress(boolean isCorrect) {
        this.completedItems = (this.completedItems == null ? 0 : this.completedItems) + 1;
        if (isCorrect) {
            this.correctItems = (this.correctItems == null ? 0 : this.correctItems) + 1;
        }
        this.lastActivityAt = LocalDateTime.now();
        recalculateAccuracyRate();
    }

    /**
     * Recalculate accuracy rate based on correct/completed items
     */
    public void recalculateAccuracyRate() {
        if (this.completedItems != null && this.completedItems > 0) {
            int correct = this.correctItems == null ? 0 : this.correctItems;
            this.accuracyRate = (float) correct / this.completedItems;
        }
    }

    // TODO: add xp_earned field later on
}
