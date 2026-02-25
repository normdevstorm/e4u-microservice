package com.e4u.lesson_service.services.evaluation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Result of evaluating a user's exercise answer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResult {

    /**
     * Whether the answer is correct
     */
    private boolean correct;

    /**
     * Score earned (0-100)
     */
    private int score;

    /**
     * Feedback message to display to user
     */
    private String feedbackMessage;

    /**
     * The correct answer (may be hidden until max attempts)
     */
    private String correctAnswer;

    /**
     * Hint for next attempt
     */
    private String hint;

    /**
     * Detailed explanation
     */
    private String explanation;

    /**
     * Alternative acceptable answers
     */
    private List<String> alternatives;

    /**
     * Grammar notes (for AI-evaluated exercises)
     */
    private String grammarNotes;

    /**
     * Whether this answer was close but not exact (for partial credit)
     */
    private boolean partialMatch;

    /**
     * Normalized/processed version of user's answer
     */
    private String normalizedUserAnswer;

    /**
     * Create a correct result with full score
     */
    public static EvaluationResult correct(String message) {
        return EvaluationResult.builder()
                .correct(true)
                .score(100)
                .feedbackMessage(message)
                .build();
    }

    /**
     * Create a correct result with custom score
     */
    public static EvaluationResult correct(String message, int score) {
        return EvaluationResult.builder()
                .correct(true)
                .score(score)
                .feedbackMessage(message)
                .build();
    }

    /**
     * Create an incorrect result with hint
     */
    public static EvaluationResult incorrect(String message, String hint) {
        return EvaluationResult.builder()
                .correct(false)
                .score(0)
                .feedbackMessage(message)
                .hint(hint)
                .build();
    }

    /**
     * Create an incorrect result revealing the answer (after max attempts)
     */
    public static EvaluationResult incorrectWithAnswer(String message, String correctAnswer, String explanation) {
        return EvaluationResult.builder()
                .correct(false)
                .score(0)
                .feedbackMessage(message)
                .correctAnswer(correctAnswer)
                .explanation(explanation)
                .build();
    }
}
