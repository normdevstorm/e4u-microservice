package com.e4u.learning_service.services.evaluation.strategies;

import com.e4u.learning_service.entities.LessonExercise;
import com.e4u.learning_service.entities.pojos.ExerciseData;
import com.e4u.learning_service.entities.pojos.SentenceBuildingExerciseData;
import com.e4u.learning_service.services.evaluation.EvaluationResult;
import com.e4u.learning_service.services.evaluation.ExerciseEvaluationStrategy;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Evaluator for SENTENCE_BUILDING exercises.
 * User reorders scrambled word blocks to form the correct sentence.
 */
@Component
public class SentenceBuildingEvaluator implements ExerciseEvaluationStrategy {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public LessonExercise.ExerciseType getExerciseType() {
        return LessonExercise.ExerciseType.SENTENCE_BUILDING;
    }

    @Override
    public EvaluationResult evaluate(ExerciseData exerciseData, Object userAnswer, int attemptNumber, int maxAttempts) {
        SentenceBuildingExerciseData data = (SentenceBuildingExerciseData) exerciseData;

        List<String> userWords = parseUserAnswer(userAnswer);
        String targetSentence = data.getTargetSentence();
        List<String> correctWords = Arrays.asList(targetSentence.split("\\s+"));

        boolean isCorrect = compareWordLists(userWords, correctWords);
        boolean isLastAttempt = attemptNumber >= maxAttempts;

        String userSentence = String.join(" ", userWords);

        if (isCorrect) {
            return EvaluationResult.builder()
                    .correct(true)
                    .score(calculateScore(attemptNumber))
                    .feedbackMessage("Perfect! You built the sentence correctly!")
                    .normalizedUserAnswer(userSentence)
                    .build();
        }

        // Calculate how many words are in correct position
        int correctPositions = countCorrectPositions(userWords, correctWords);
        boolean isPartialMatch = correctPositions > 0;

        if (isLastAttempt) {
            return EvaluationResult.builder()
                    .correct(false)
                    .score(0)
                    .feedbackMessage("The correct sentence is:")
                    .correctAnswer(targetSentence)
                    .normalizedUserAnswer(userSentence)
                    .partialMatch(isPartialMatch)
                    .build();
        }

        // More attempts remaining
        String hint = generateHint(correctWords, userWords, attemptNumber);
        String message = isPartialMatch
                ? String.format("Good progress! %d word(s) are in the right position.", correctPositions)
                : "Not quite right. Try rearranging the words.";

        return EvaluationResult.builder()
                .correct(false)
                .score(0)
                .feedbackMessage(message)
                .hint(hint)
                .normalizedUserAnswer(userSentence)
                .partialMatch(isPartialMatch)
                .build();
    }

    @Override
    public int getDefaultMaxAttempts() {
        return 3;
    }

    private List<String> parseUserAnswer(Object userAnswer) {
        if (userAnswer == null) {
            return List.of();
        }

        if (userAnswer instanceof List) {
            return ((List<?>) userAnswer).stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }

        if (userAnswer instanceof String) {
            String answerStr = (String) userAnswer;
            // Try to parse as JSON array
            if (answerStr.startsWith("[")) {
                try {
                    return objectMapper.readValue(answerStr, new TypeReference<List<String>>() {
                    });
                } catch (Exception e) {
                    // Fall through to split by space
                }
            }
            return Arrays.asList(answerStr.split("\\s+"));
        }

        return List.of(userAnswer.toString());
    }

    private boolean compareWordLists(List<String> userWords, List<String> correctWords) {
        if (userWords.size() != correctWords.size()) {
            return false;
        }

        for (int i = 0; i < userWords.size(); i++) {
            if (!normalizeAnswer(userWords.get(i)).equals(normalizeAnswer(correctWords.get(i)))) {
                return false;
            }
        }
        return true;
    }

    private int countCorrectPositions(List<String> userWords, List<String> correctWords) {
        int count = 0;
        int minLen = Math.min(userWords.size(), correctWords.size());

        for (int i = 0; i < minLen; i++) {
            if (normalizeAnswer(userWords.get(i)).equals(normalizeAnswer(correctWords.get(i)))) {
                count++;
            }
        }
        return count;
    }

    private String generateHint(List<String> correctWords, List<String> userWords, int attemptNumber) {
        if (attemptNumber == 1) {
            return String.format("Hint: The sentence starts with '%s'", correctWords.get(0));
        }

        if (attemptNumber >= 2 && correctWords.size() >= 2) {
            return String.format("Hint: The sentence starts with '%s %s'",
                    correctWords.get(0), correctWords.get(1));
        }

        return "Think about the sentence structure.";
    }
}
