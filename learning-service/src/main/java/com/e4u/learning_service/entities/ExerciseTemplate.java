package com.e4u.learning_service.entities;

import com.e4u.learning_service.common.constants.Constant;
import com.e4u.learning_service.entities.pojos.ExerciseData;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a static exercise definition.
 * Contains the exercise content and correct answer.
 * Supports both shared templates and user-specific generated exercises.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "exercise_templates", indexes = {
    @Index(name = "idx_exercise_lesson", columnList = "lesson_template_id"),
    @Index(name = "idx_exercise_word_context", columnList = "word_context_template_id"),
    @Index(name = "idx_exercise_user", columnList = "created_for_user_id")
})
public class ExerciseTemplate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_template_id")
    private LessonTemplate lessonTemplate;

    /**
     * The word context being tested in this exercise (optional for some exercise types)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_context_template_id")
    private WordContextTemplate wordContextTemplate;

    @Enumerated(EnumType.STRING)
    @Column(name = "exercise_type", nullable = false, length = 255)
    private ExerciseType exerciseType;

    /**
     * JSONB payload containing exercise-specific data.
     * Uses polymorphic ExerciseData with correctAnswer embedded.
     * Supported types: CONTEXTUAL_DISCOVERY, MULTIPLE_CHOICE, MECHANIC_DRILL,
     * TARGET_WORD_INTEGRATION, SENTENCE_BUILDING, ASSISTED_COMPOSITION, CLOZE_WITH_AUDIO
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "exercise_payload", columnDefinition = "jsonb", nullable = false)
    private ExerciseData exerciseData;

    /**
     * If NULL: This is a shared template available to all users.
     * If has value: This is a custom exercise generated for a specific user.
     */
    @Column(name = "created_for_user_id")
    private UUID createdForUserId;

    /**
     * User attempts on this exercise template
     */
    @OneToMany(mappedBy = "exerciseTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserExerciseAttempt> userAttempts = new ArrayList<>();

    @Getter
    public enum ExerciseType {
        CONTEXTUAL_DISCOVERY(Constant.CONTEXTUAL_DISCOVERY_IDENTIFIER),
        MULTIPLE_CHOICE(Constant.MULTIPLE_CHOICE_IDENTIFIER),
        MECHANIC_DRILL(Constant.MECHANIC_DRILL_IDENTIFIER),
        TARGET_WORD_INTEGRATION(Constant.TARGET_WORD_INTEGRATION_IDENTIFIER),
        SENTENCE_BUILDING(Constant.SENTENCE_BUILDING_IDENTIFIER),
        ASSISTED_COMPOSITION(Constant.ASSISTED_COMPOSITION_IDENTIFIER),
        CLOZE_WITH_AUDIO(Constant.CLOZE_WITH_AUDIO_IDENTIFIER),
        ;

        private final String subtypeIdentifier;

        ExerciseType(String subtypeIdentifier) {
            this.subtypeIdentifier = subtypeIdentifier;
        }
    }

    /**
     * Check if this is a shared/system template
     */
    public boolean isSharedTemplate() {
        return createdForUserId == null;
    }

    /**
     * Check if this template belongs to a specific user
     */
    public boolean isUserSpecificTemplate(UUID userId) {
        return createdForUserId != null && createdForUserId.equals(userId);
    }
}
