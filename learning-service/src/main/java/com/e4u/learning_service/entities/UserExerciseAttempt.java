package com.e4u.learning_service.entities;

import com.e4u.learning_service.converters.JsonbConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.Map;
import java.util.UUID;

/**
 * Entity recording a user's attempt at a specific exercise.
 * Captures the user's answer and whether it was correct.
 * Replaces the user-specific fields from old LessonExercise entity.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_exercise_attempts", indexes = {
    @Index(name = "idx_attempt_session", columnList = "session_id"),
    @Index(name = "idx_attempt_exercise", columnList = "exercise_template_id")
})
public class UserExerciseAttempt extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private UserLessonSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_template_id", nullable = false)
    private ExerciseTemplate exerciseTemplate;

    /**
     * JSONB containing the user's submitted answer.
     * Structure matches the exerciseType format.
     */
    @Convert(converter = JsonbConverter.class)
    @Column(name = "user_answer", columnDefinition = "jsonb")
    private Map<String, Object> userAnswer;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    /**
     * Optional score for partial credit exercises
     */
    @Column(name = "score")
    private Integer score;

    /**
     * Time taken to complete this exercise (in seconds)
     */
    @Column(name = "time_taken_seconds")
    private Integer timeTakenSeconds;

    /**
     * Record the user's answer. Note: Validation should be done externally
     * using exercise evaluator strategies.
     */
    public void submitAnswer(Map<String, Object> answer) {
        this.userAnswer = answer;
        // Note: isCorrect should be set after external validation
    }

    /**
     * Record answer with validation result and update session
     */
    public void submitAnswer(Map<String, Object> answer, boolean correct) {
        this.userAnswer = answer;
        this.isCorrect = correct;
        
        // Update session progress
        if (session != null) {
            session.recordExerciseCompletion(this.isCorrect);
        }
    }

    /**
     * Get the user ID from the session
     */
    public UUID getUserId() {
        return session != null ? session.getUserId() : null;
    }

    /**
     * Get the exercise type from the template
     */
    public ExerciseTemplate.ExerciseType getExerciseType() {
        return exerciseTemplate != null ? exerciseTemplate.getExerciseType() : null;
    }

    /**
     * Check if this attempt has been submitted
     */
    public boolean isSubmitted() {
        return userAnswer != null;
    }
}
