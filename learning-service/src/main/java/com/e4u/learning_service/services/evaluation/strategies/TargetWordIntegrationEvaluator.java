package com.e4u.learning_service.services.evaluation.strategies;

import com.e4u.learning_service.entities.ExerciseTemplate;
import com.e4u.learning_service.entities.pojos.ExerciseData;
import com.e4u.learning_service.entities.pojos.TargetWordIntegrationExerciseData;
import com.e4u.learning_service.services.evaluation.EvaluationResult;
import com.e4u.learning_service.services.evaluation.ExerciseEvaluationStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Evaluator for TARGET_WORD_INTEGRATION exercises (formerly MICRO_TASK_OUTPUT).
 * User writes a full sentence using the target word.
 * 
 * This is a more complex evaluation that checks:
 * 1. Target word is present
 * 2. Word count is within range
 * 3. (Future: AI evaluation for grammar and context)
 */
@Component
public class TargetWordIntegrationEvaluator implements ExerciseEvaluationStrategy {

    @Override
    public ExerciseTemplate.ExerciseType getExerciseType() {
        return ExerciseTemplate.ExerciseType.TARGET_WORD_INTEGRATION;
    }

    @Override
    public EvaluationResult evaluate(ExerciseData exerciseData, Object userAnswer, int attemptNumber, int maxAttempts) {
        TargetWordIntegrationExerciseData data = (TargetWordIntegrationExerciseData) exerciseData;

        String userSentence = userAnswer != null ? userAnswer.toString().trim() : "";
        String targetWord = data.getTargetWord();
        Integer minWords = data.getMinWords() != null ? data.getMinWords() : 3;
        Integer maxWords = data.getMaxWords() != null ? data.getMaxWords() : 20;

        // Validation checks
        List<String> validationErrors = validateSentence(userSentence, targetWord, minWords, maxWords);

        boolean isLastAttempt = attemptNumber >= maxAttempts;

        if (validationErrors.isEmpty()) {
            // Basic validation passed - in production, send to AI for deeper evaluation
            // For now, we consider it correct if it passes basic checks
            return EvaluationResult.builder()
                    .correct(true)
                    .score(calculateScore(attemptNumber))
                    .feedbackMessage("Good job! Your sentence uses the target word correctly.")
                    .normalizedUserAnswer(userSentence)
                    .build();
        }

        // Has validation errors
        String errorMessage = validationErrors.get(0);

        if (isLastAttempt) {
            return EvaluationResult.builder()
                    .correct(false)
                    .score(0)
                    .feedbackMessage(errorMessage)
                    .correctAnswer(data.getExampleResponse())
                    .explanation("Here's an example of a good response.")
                    .normalizedUserAnswer(userSentence)
                    .build();
        }

        // More attempts remaining
        String hint = buildHint(data, validationErrors, attemptNumber);

        return EvaluationResult.builder()
                .correct(false)
                .score(0)
                .feedbackMessage(errorMessage)
                .hint(hint)
                .normalizedUserAnswer(userSentence)
                .build();
    }

    @Override
    public int getDefaultMaxAttempts() {
        return 2; // More lenient for creative exercises
    }

    private List<String> validateSentence(String sentence, String targetWord, int minWords, int maxWords) {
        java.util.ArrayList<String> errors = new java.util.ArrayList<>();

        if (sentence == null || sentence.isBlank()) {
            errors.add("Please write a sentence.");
            return errors;
        }

        // Check word count
        String[] words = sentence.split("\\s+");
        int wordCount = words.length;

        if (wordCount < minWords) {
            errors.add(String.format("Your sentence is too short. Please write at least %d words.", minWords));
            return errors;
        }

        if (wordCount > maxWords) {
            errors.add(String.format("Your sentence is too long. Please keep it under %d words.", maxWords));
            return errors;
        }

        // Check target word presence (case-insensitive)
        String lowerSentence = sentence.toLowerCase();
        String lowerTarget = targetWord.toLowerCase();

        if (!lowerSentence.contains(lowerTarget)) {
            errors.add(String.format("Make sure to use the word '%s' in your sentence.", targetWord));
            return errors;
        }

        return errors;
    }

    private String buildHint(TargetWordIntegrationExerciseData data, List<String> errors, int attemptNumber) {
        if (data.getContextHints() != null && !data.getContextHints().isEmpty()) {
            return String.format("Hint: Think about these related words: %s",
                    String.join(", ", data.getContextHints()));
        }

        if (data.getSimplifiedSentence() != null) {
            return String.format("Hint: %s", data.getSimplifiedSentence());
        }

        return String.format("Try using '%s' in a sentence about its meaning or context.",
                data.getTargetWord());
    }
}
