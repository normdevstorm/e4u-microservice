package com.e4u.lesson_service.services.evaluation.strategies;

import com.e4u.lesson_service.entities.LessonExercise;
import com.e4u.lesson_service.entities.pojos.ExerciseData;
import com.e4u.lesson_service.services.evaluation.EvaluationResult;
import com.e4u.lesson_service.services.evaluation.ExerciseEvaluationStrategy;
import org.springframework.stereotype.Component;

/**
 * Evaluator for CONTEXTUAL_DISCOVERY exercises.
 * This is a passive exercise - user just acknowledges they've seen/heard the
 * word.
 * Always marks as correct.
 */
@Component
public class ContextualDiscoveryEvaluator implements ExerciseEvaluationStrategy {

    private static final String ACKNOWLEDGED = "ACKNOWLEDGED";

    @Override
    public LessonExercise.ExerciseType getExerciseType() {
        return LessonExercise.ExerciseType.CONTEXTUAL_DISCOVERY;
    }

    @Override
    public EvaluationResult evaluate(ExerciseData exerciseData, Object userAnswer, int attemptNumber, int maxAttempts) {
        // Contextual discovery is passive - always correct
        return EvaluationResult.builder()
                .correct(true)
                .score(100)
                .feedbackMessage("Great! You've seen this word in context.")
                .normalizedUserAnswer(ACKNOWLEDGED)
                .build();
    }

    @Override
    public int getDefaultMaxAttempts() {
        return 1; // Single attempt for passive exercises
    }
}
