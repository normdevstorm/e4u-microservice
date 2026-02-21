package com.e4u.lesson_service.entities;

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
    }

    public void completeLesson(Float accuracyRate) {
        this.status = LessonStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.accuracyRate = accuracyRate;
    }

    // TODO: add xp_earned field later on
}
