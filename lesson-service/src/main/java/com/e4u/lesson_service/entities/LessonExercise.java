package com.e4u.lesson_service.entities;

import com.e4u.lesson_service.common.constants.Constant;
import com.e4u.lesson_service.entities.pojos.ExerciseData;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "lesson_exercise")
public class LessonExercise extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private DynamicLesson lesson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_instance_id")
    private UserVocabInstance wordInstance;

    @Enumerated(EnumType.STRING)
    @Column(name = "exercise_type", nullable = false)
    private ExerciseType exerciseType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "exercise_data", columnDefinition = "jsonb")
    private ExerciseData exerciseData;

    @Column(name = "sequence_order")
    private Integer sequenceOrder;

    @Column(name = "is_completed")
    @Builder.Default
    private Boolean isCompleted = false;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "user_answer")
    private String userAnswer;

    @Column(name = "time_spent_seconds")
    private Integer timeSpentSeconds;

    @Getter
    public enum ExerciseType {
        CONTEXTUAL_DISCOVERY(Constant.CONTEXTUAL_DISCOVERY_IDENTIFIER),
        MULTIPLE_CHOICE(Constant.MULTIPLE_CHOICE_IDENTIFIER),
        MECHANIC_DRILL(Constant.MECHANIC_DRILL_IDENTIFIER),
        MICRO_TASK_OUTPUT(Constant.MICRO_TASK_OUTPUT_IDENTIFIER),
        SENTENCE_BUILDING(Constant.SENTENCE_BUILDING_IDENTIFIER),
        PARTIAL_OUTPUT(Constant.PARTIAL_OUTPUT_IDENTIFIER),
        CLOZE_WITH_AUDIO(Constant.CLOZE_WITH_AUDIO_IDENTIFIER);

        private final String subtypeIdentifier;

        ExerciseType(String subtypeIdentifier) {
            this.subtypeIdentifier = subtypeIdentifier;
        }
    }
}
