package com.e4u.learning_service.services.evaluation.strategies;

import com.e4u.learning_service.entities.ExerciseTemplate;
import com.e4u.learning_service.entities.pojos.AssistedCompositionExerciseData;
import com.e4u.learning_service.entities.pojos.ExerciseData;
import com.e4u.learning_service.services.evaluation.EvaluationResult;
import com.e4u.learning_service.services.evaluation.ExerciseEvaluationStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Evaluator for ASSISTED_COMPOSITION exercises (formerly PARTIAL_OUTPUT).
 * User fills in the target word in a sentence skeleton.
 */
@Component
public class AssistedCompositionEvaluator implements ExerciseEvaluationStrategy {

    @Override
    public ExerciseTemplate.ExerciseType getExerciseType() {
        return ExerciseTemplate.ExerciseType.ASSISTED_COMPOSITION;
    }

    @Override
    public EvaluationResult evaluate(ExerciseData exerciseData, Object userAnswer, int attemptNumber, int maxAttempts) {
        AssistedCompositionExerciseData data = (AssistedCompositionExerciseData) exerciseData;

        String userAnswerStr = userAnswer != null ? userAnswer.toString() : "";
        String normalizedUserAnswer = normalizeAnswer(userAnswerStr);
        String expectedWord = data.getExpectedWord();
        String normalizedExpected = normalizeAnswer(expectedWord);

        // Check primary expected word
        boolean isCorrect = normalizedUserAnswer.equals(normalizedExpected);

        // Check alternatives if primary doesn't match
        List<String> alternatives = data.getAlternativeAnswers();
        if (!isCorrect && alternatives != null) {
            for (String alt : alternatives) {
                if (normalizedUserAnswer.equals(normalizeAnswer(alt))) {
                    isCorrect = true;
                    break;
                }
            }
        }

        boolean isLastAttempt = attemptNumber >= maxAttempts;

        if (isCorrect) {
            String feedback = data.getCorrectFeedback() != null
                    ? data.getCorrectFeedback()
                    : "Correct! Great job!";

            return EvaluationResult.builder()
                    .correct(true)
                    .score(calculateScore(attemptNumber))
                    .feedbackMessage(feedback)
                    .alternatives(alternatives)
                    .normalizedUserAnswer(normalizedUserAnswer)
                    .build();
        }

        // Check for close match
        boolean isCloseMatch = isCloseMatch(normalizedUserAnswer, normalizedExpected);

        if (isLastAttempt) {
            return EvaluationResult.builder()
                    .correct(false)
                    .score(0)
                    .feedbackMessage("The correct answer is:")
                    .correctAnswer(expectedWord)
                    .alternatives(alternatives)
                    .explanation(buildExplanation(data))
                    .normalizedUserAnswer(normalizedUserAnswer)
                    .partialMatch(isCloseMatch)
                    .build();
        }

        // More attempts remaining
        String hint = buildHint(data, attemptNumber, normalizedExpected);
        String message = isCloseMatch
                ? "Almost! Check your spelling."
                : "Not quite. Try again!";

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

    private String buildHint(AssistedCompositionExerciseData data, int attemptNumber, String normalizedExpected) {
        if (data.getHint() != null && attemptNumber == 1) {
            return data.getHint();
        }

        if (attemptNumber >= 2 && normalizedExpected.length() > 2) {
            return String.format("Hint: The word starts with '%s'",
                    normalizedExpected.substring(0, Math.min(3, normalizedExpected.length())));
        }

        return "Think about the context of the sentence.";
    }

    private String buildExplanation(AssistedCompositionExerciseData data) {
        StringBuilder sb = new StringBuilder();
        sb.append("Complete sentence: ");
        if (data.getSetupText() != null) {
            sb.append(data.getSetupText().replace("_", data.getExpectedWord()));
        }
        return sb.toString();
    }

    private boolean isCloseMatch(String userAnswer, String correctAnswer) {
        if (userAnswer == null || correctAnswer == null)
            return false;
        if (Math.abs(userAnswer.length() - correctAnswer.length()) > 2)
            return false;

        int distance = levenshteinDistance(userAnswer, correctAnswer);
        return distance > 0 && distance <= 2;
    }

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
