package com.e4u.learning_service.services.evaluation.strategies;

import com.e4u.learning_service.entities.ExerciseTemplate;
import com.e4u.learning_service.entities.pojos.AssistedCompositionExerciseData;
import com.e4u.learning_service.entities.pojos.ExerciseData;
import com.e4u.learning_service.entities.pojos.answers.AssistedCompositionAnswer;
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

        // Extract the composition text from the typed answer
        String compositionText;
        if (userAnswer instanceof AssistedCompositionAnswer answer) {
            compositionText = answer.getComposition() != null ? answer.getComposition() : "";
        } else {
            compositionText = userAnswer != null ? userAnswer.toString() : "";
        }
        compositionText = compositionText.trim();

        String normalizedComposition = normalizeAnswer(compositionText);
        String expectedWord = data.getExpectedWord();
        String normalizedExpected = normalizeAnswer(expectedWord);
        List<String> alternatives = data.getAlternativeAnswers();

        // Correctness rule 1: composition must CONTAIN the expected word (or an
        // alternative)
        boolean containsExpected = normalizedComposition.contains(normalizedExpected);
        if (!containsExpected && alternatives != null) {
            for (String alt : alternatives) {
                if (normalizedComposition.contains(normalizeAnswer(alt))) {
                    containsExpected = true;
                    break;
                }
            }
        }

        // Correctness rule 2: composition must meet the minimum word count
        int wordCount = compositionText.isEmpty() ? 0 : compositionText.split("\\s+").length;
        int minWordCount = data.getMinWordCount() != null ? data.getMinWordCount() : 1;
        boolean meetsWordCount = wordCount >= minWordCount;

        boolean isCorrect = containsExpected && meetsWordCount;
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
                    .normalizedUserAnswer(compositionText)
                    .build();
        }

        // Build specific error feedback based on which rule(s) failed
        String errorMessage;
        if (!containsExpected && !meetsWordCount) {
            errorMessage = String.format(
                    "Your composition needs at least %d word(s) and must include the word '%s'.",
                    minWordCount, expectedWord);
        } else if (!containsExpected) {
            errorMessage = String.format(
                    "Make sure to include the word '%s' in your composition.", expectedWord);
        } else {
            errorMessage = String.format(
                    "Your composition needs at least %d word(s).", minWordCount);
        }

        if (isLastAttempt) {
            return EvaluationResult.builder()
                    .correct(false)
                    .score(0)
                    .feedbackMessage(errorMessage)
                    .correctAnswer(expectedWord)
                    .alternatives(alternatives)
                    .explanation(buildExplanation(data))
                    .normalizedUserAnswer(compositionText)
                    .build();
        }

        // More attempts remaining
        String hint = buildHint(data, attemptNumber, normalizedExpected);

        return EvaluationResult.builder()
                .correct(false)
                .score(0)
                .feedbackMessage(errorMessage)
                .hint(hint)
                .normalizedUserAnswer(compositionText)
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

}
