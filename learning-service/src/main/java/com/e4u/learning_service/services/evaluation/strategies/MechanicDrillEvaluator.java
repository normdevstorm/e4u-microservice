package com.e4u.learning_service.services.evaluation.strategies;

import com.e4u.learning_service.entities.ExerciseTemplate;
import com.e4u.learning_service.entities.pojos.ExerciseData;
import com.e4u.learning_service.entities.pojos.MechanicDrillExerciseData;
import com.e4u.learning_service.services.evaluation.EvaluationResult;
import com.e4u.learning_service.services.evaluation.ExerciseEvaluationStrategy;
import org.springframework.stereotype.Component;

/**
 * Evaluator for MECHANIC_DRILL exercises.
 * User fills in a blank in a sentence template.
 */
@Component
public class MechanicDrillEvaluator implements ExerciseEvaluationStrategy {

    @Override
    public ExerciseTemplate.ExerciseType getExerciseType() {
        return ExerciseTemplate.ExerciseType.MECHANIC_DRILL;
    }

    @Override
    public EvaluationResult evaluate(ExerciseData exerciseData, Object userAnswer, int attemptNumber, int maxAttempts) {
        MechanicDrillExerciseData data = (MechanicDrillExerciseData) exerciseData;

        String userAnswerStr = userAnswer != null ? userAnswer.toString() : "";
        String normalizedUserAnswer = normalizeAnswer(userAnswerStr);
        String correctAnswer = data.getCorrectAnswer();
        String normalizedCorrect = normalizeAnswer(correctAnswer);

        boolean isCorrect = normalizedUserAnswer.equals(normalizedCorrect);
        boolean isLastAttempt = attemptNumber >= maxAttempts;

        if (isCorrect) {
            return EvaluationResult.builder()
                    .correct(true)
                    .score(calculateScore(attemptNumber))
                    .feedbackMessage("Perfect! You got it right!")
                    .normalizedUserAnswer(normalizedUserAnswer)
                    .build();
        }

        // Check for close match (typo tolerance)
        boolean isCloseMatch = isCloseMatch(normalizedUserAnswer, normalizedCorrect);

        if (isLastAttempt) {
            return EvaluationResult.builder()
                    .correct(false)
                    .score(0)
                    .feedbackMessage("The correct answer is:")
                    .correctAnswer(correctAnswer)
                    .hint(data.getHint())
                    .normalizedUserAnswer(normalizedUserAnswer)
                    .partialMatch(isCloseMatch)
                    .build();
        }

        // More attempts remaining
        String hint = generateProgressiveHint(correctAnswer, attemptNumber);
        String message = isCloseMatch
                ? "Very close! Check your spelling."
                : "Not quite right. Try again!";

        return EvaluationResult.builder()
                .correct(false)
                .score(0)
                .feedbackMessage(message)
                .hint(hint)
                .normalizedUserAnswer(normalizedUserAnswer)
                .partialMatch(isCloseMatch)
                .build();
    }

    @Override
    public int getDefaultMaxAttempts() {
        return 3;
    }

    /**
     * Check if the answer is close (within 2 character edits)
     */
    private boolean isCloseMatch(String userAnswer, String correctAnswer) {
        if (userAnswer == null || correctAnswer == null)
            return false;
        if (Math.abs(userAnswer.length() - correctAnswer.length()) > 2)
            return false;

        int distance = levenshteinDistance(userAnswer, correctAnswer);
        return distance > 0 && distance <= 2;
    }

    /**
     * Calculate Levenshtein distance between two strings
     */
    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;
                    dp[i][j] = Math.min(
                            Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                            dp[i - 1][j - 1] + cost);
                }
            }
        }
        return dp[s1.length()][s2.length()];
    }
}
