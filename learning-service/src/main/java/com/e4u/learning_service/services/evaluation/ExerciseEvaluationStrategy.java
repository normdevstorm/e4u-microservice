package com.e4u.learning_service.services.evaluation;

import com.e4u.learning_service.entities.LessonExercise;
import com.e4u.learning_service.entities.pojos.ExerciseData;

/**
 * Strategy interface for evaluating exercise answers.
 * Each exercise type has its own implementation.
 */
public interface ExerciseEvaluationStrategy {

    /**
     * Get the exercise type this strategy handles
     */
    LessonExercise.ExerciseType getExerciseType();

    /**
     * Evaluate the user's answer against the exercise data
     *
     * @param exerciseData  The exercise configuration/data
     * @param userAnswer    The user's submitted answer
     * @param attemptNumber Current attempt number (1-based)
     * @param maxAttempts   Maximum attempts allowed
     * @return Evaluation result with correctness, score, and feedback
     */
    EvaluationResult evaluate(ExerciseData exerciseData, Object userAnswer, int attemptNumber, int maxAttempts);

    /**
     * Get the maximum attempts for this exercise type (default: 3)
     */
    default int getDefaultMaxAttempts() {
        return 3;
    }

    /**
     * Calculate score based on attempt number
     * Default: 100% for 1st, 70% for 2nd, 40% for 3rd
     */
    default int calculateScore(int attemptNumber) {
        return switch (attemptNumber) {
            case 1 -> 100;
            case 2 -> 70;
            case 3 -> 40;
            default -> 20;
        };
    }

    /**
     * Normalize the answer for comparison (trim, lowercase, etc.)
     */
    default String normalizeAnswer(String answer) {
        if (answer == null)
            return "";
        return answer.trim().toLowerCase();
    }

    /**
     * Generate a progressive hint based on attempt number
     */
    default String generateProgressiveHint(String correctAnswer, int attemptNumber) {
        if (correctAnswer == null || correctAnswer.isEmpty()) {
            return "Try again!";
        }

        return switch (attemptNumber) {
            case 1 -> "Not quite. Think about the context.";
            case 2 -> String.format("Hint: The answer starts with '%s'",
                    correctAnswer.substring(0, Math.min(2, correctAnswer.length())));
            default -> String.format("The answer is: %s", correctAnswer);
        };
    }
}
