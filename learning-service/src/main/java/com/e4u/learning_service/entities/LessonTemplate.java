package com.e4u.learning_service.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Entity representing a static lesson definition within a curriculum unit.
 * Part of the blueprint layer - defines what a lesson contains,
 * separate from user's execution state.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "lesson_templates", indexes = {
    @Index(name = "idx_lesson_unit", columnList = "unit_id, sequence_order")
})
public class LessonTemplate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    private CurriculumUnit unit;

    @Column(name = "lesson_name", nullable = false, length = 255)
    private String lessonName;

    @Enumerated(EnumType.STRING)
    @Column(name = "lesson_type", length = 50)
    @Builder.Default
    private LessonType lessonType = LessonType.STANDARD;

    /**
     * Order of this lesson within its unit
     */
    @Column(name = "sequence_order")
    @Builder.Default
    private Integer sequenceOrder = 0;

    /**
     * Word context templates associated with this lesson (Many-to-Many)
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "lesson_template_word_contexts",
        joinColumns = @JoinColumn(name = "lesson_template_id"),
        inverseJoinColumns = @JoinColumn(name = "word_context_template_id")
    )
    @Builder.Default
    private Set<WordContextTemplate> wordContextTemplates = new HashSet<>();

    /**
     * Exercise templates associated with this lesson.
     * Can be shared templates or user-specific generated exercises.
     */
    @OneToMany(mappedBy = "lessonTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ExerciseTemplate> exerciseTemplates = new ArrayList<>();

    /**
     * User sessions for this lesson template
     */
    @OneToMany(mappedBy = "lessonTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserLessonSession> userSessions = new ArrayList<>();

    public enum LessonType {
        STANDARD,       // Regular vocabulary lesson
        REVIEW,         // Spaced repetition review
        ASSESSMENT,     // Progress assessment/quiz
        INTRODUCTION    // Unit introduction lesson
    }

    /**
     * Add an exercise template to this lesson
     */
    public void addExerciseTemplate(ExerciseTemplate exercise) {
        exerciseTemplates.add(exercise);
        exercise.setLessonTemplate(this);
    }

    /**
     * Remove an exercise template from this lesson
     */
    public void removeExerciseTemplate(ExerciseTemplate exercise) {
        exerciseTemplates.remove(exercise);
        exercise.setLessonTemplate(null);
    }

    /**
     * Add a word context template to this lesson
     */
    public void addWordContextTemplate(WordContextTemplate wordContext) {
        wordContextTemplates.add(wordContext);
        wordContext.getLessonTemplates().add(this);
    }

    /**
     * Remove a word context template from this lesson
     */
    public void removeWordContextTemplate(WordContextTemplate wordContext) {
        wordContextTemplates.remove(wordContext);
        wordContext.getLessonTemplates().remove(this);
    }

    /**
     * Get shared (non-user-specific) exercise templates
     */
    public List<ExerciseTemplate> getSharedExerciseTemplates() {
        return exerciseTemplates.stream()
            .filter(ExerciseTemplate::isSharedTemplate)
            .toList();
    }

    /**
     * Get exercise templates for a specific user (including shared ones)
     */
    public List<ExerciseTemplate> getExerciseTemplatesForUser(UUID userId) {
        return exerciseTemplates.stream()
            .filter(e -> e.isSharedTemplate() || e.isUserSpecificTemplate(userId))
            .toList();
    }
}
