package com.e4u.learning_service.services.evaluation.strategies;

import com.e4u.learning_service.entities.LessonExercise;
import com.e4u.learning_service.entities.pojos.ClozeWithAudioExerciseData;
import com.e4u.learning_service.entities.pojos.ExerciseData;
import com.e4u.learning_service.services.evaluation.EvaluationResult;
import com.e4u.learning_service.services.evaluation.ExerciseEvaluationStrategy;
import org.springframework.stereotype.Component;

/**
 * Evaluator for CLOZE_WITH_AUDIO exercises.
 * User listens to audio and fills in the missing word.
 */
@Component
public class ClozeWithAudioEvaluator implements ExerciseEvaluationStrategy {

    @Override
    public LessonExercise.ExerciseType getExerciseType() {
        return LessonExercise.ExerciseType.CLOZE_WITH_AUDIO;
    }

    @Override
    public EvaluationResult evaluate(ExerciseData exerciseData, Object userAnswer, int attemptNumber, int maxAttempts) {
        ClozeWithAudioExerciseData data = (ClozeWithAudioExerciseData) exerciseData;

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
                    .feedbackMessage("Excellent! You heard it correctly!")
                    .normalizedUserAnswer(normalizedUserAnswer)
                    .build();
        }

        // Check for close match (phonetic similarity)
        boolean isCloseMatch = isCloseMatch(normalizedUserAnswer, normalizedCorrect);

        if (isLastAttempt) {
            return EvaluationResult.builder()
                    .correct(false)
                    .score(0)
                    .feedbackMessage("The correct word is:")
                    .correctAnswer(correctAnswer)
                    .hint("Try listening to the audio again to hear how it's pronounced.")
                    .normalizedUserAnswer(normalizedUserAnswer)
                    .partialMatch(isCloseMatch)
                    .build();
        }

        // More attempts remaining
        String hint = generateHint(correctAnswer, attemptNumber);
        String message = isCloseMatch
                ? "Very close! Listen again carefully."
                : "Not quite. Listen to the audio again.";

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

    private String generateHint(String correctAnswer, int attemptNumber) {
        if (attemptNumber == 1) {
            return "Listen carefully to each syllable.";
        }

        if (attemptNumber >= 2 && correctAnswer != null && correctAnswer.length() > 2) {
            return String.format("The word starts with '%s' and has %d letters.",
                    correctAnswer.substring(0, 1).toUpperCase(), correctAnswer.length());
        }

        return "This is your last chance. Listen very carefully!";
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
