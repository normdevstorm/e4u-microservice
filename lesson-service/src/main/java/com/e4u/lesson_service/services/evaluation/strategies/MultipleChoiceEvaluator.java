package com.e4u.lesson_service.services.evaluation.strategies;

import com.e4u.lesson_service.entities.LessonExercise;
import com.e4u.lesson_service.entities.pojos.ExerciseData;
import com.e4u.lesson_service.entities.pojos.MultipleChoiceExerciseData;
import com.e4u.lesson_service.services.evaluation.EvaluationResult;
import com.e4u.lesson_service.services.evaluation.ExerciseEvaluationStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Evaluator for MULTIPLE_CHOICE exercises.
 * User selects one option from a list.
 */
@Component
public class MultipleChoiceEvaluator implements ExerciseEvaluationStrategy {

    @Override
    public LessonExercise.ExerciseType getExerciseType() {
        return LessonExercise.ExerciseType.MULTIPLE_CHOICE;
    }

    @Override
    public EvaluationResult evaluate(ExerciseData exerciseData, Object userAnswer, int attemptNumber, int maxAttempts) {
        MultipleChoiceExerciseData data = (MultipleChoiceExerciseData) exerciseData;

        // Parse user's selected option index
        int selectedIndex = parseSelectedIndex(userAnswer);
        String correctAnswer = data.getCorrectAnswer();
        List<String> options = data.getOptions();

        // Find correct option index
        int correctIndex = findCorrectIndex(options, correctAnswer);

        boolean isCorrect = selectedIndex == correctIndex;
        boolean isLastAttempt = attemptNumber >= maxAttempts;

        if (isCorrect) {
            return EvaluationResult.builder()
                    .correct(true)
                    .score(calculateScore(attemptNumber))
                    .feedbackMessage("Correct! Well done!")
                    .normalizedUserAnswer(String.valueOf(selectedIndex))
                    .build();
        }

        // Wrong answer
        String selectedOption = (selectedIndex >= 0 && selectedIndex < options.size())
                ? options.get(selectedIndex)
                : "Invalid selection";

        if (isLastAttempt) {
            return EvaluationResult.builder()
                    .correct(false)
                    .score(0)
                    .feedbackMessage("The correct answer is shown below.")
                    .correctAnswer(correctAnswer)
                    .explanation(data.getQuestion())
                    .normalizedUserAnswer(selectedOption)
                    .build();
        }

        // More attempts remaining
        return EvaluationResult.builder()
                .correct(false)
                .score(0)
                .feedbackMessage("Not quite right. Try again!")
                .hint(generateHint(data, attemptNumber))
                .normalizedUserAnswer(selectedOption)
                .build();
    }

    @Override
    public int getDefaultMaxAttempts() {
        return 2;
    }

    private int parseSelectedIndex(Object userAnswer) {
        if (userAnswer == null)
            return -1;

        if (userAnswer instanceof Number) {
            return ((Number) userAnswer).intValue();
        }

        try {
            return Integer.parseInt(userAnswer.toString().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private int findCorrectIndex(List<String> options, String correctAnswer) {
        if (options == null || correctAnswer == null)
            return -1;

        for (int i = 0; i < options.size(); i++) {
            if (correctAnswer.equalsIgnoreCase(options.get(i))) {
                return i;
            }
        }
        return -1;
    }

    private String generateHint(MultipleChoiceExerciseData data, int attemptNumber) {
        if (attemptNumber == 1) {
            return "Read each option carefully and think about the context.";
        }
        return "This is your last chance. Choose wisely!";
    }
}
