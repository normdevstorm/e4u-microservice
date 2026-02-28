package com.e4u.learning_service.services.evaluation;

import com.e4u.learning_service.entities.LessonExercise;
import com.e4u.learning_service.entities.pojos.ExerciseData;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Service for evaluating exercise answers.
 * Delegates to type-specific evaluation strategies.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExerciseEvaluationService {

    private final List<ExerciseEvaluationStrategy> strategies;
    private final Map<LessonExercise.ExerciseType, ExerciseEvaluationStrategy> strategyMap = new EnumMap<>(
            LessonExercise.ExerciseType.class);

    @PostConstruct
    public void init() {
        for (ExerciseEvaluationStrategy strategy : strategies) {
            LessonExercise.ExerciseType type = strategy.getExerciseType();
            strategyMap.put(type, strategy);
            log.info("Registered evaluation strategy for exercise type: {}", type);
        }

        // Map legacy types to their new implementations
        if (strategyMap.containsKey(LessonExercise.ExerciseType.TARGET_WORD_INTEGRATION)) {
            strategyMap.put(LessonExercise.ExerciseType.MICRO_TASK_OUTPUT,
                    strategyMap.get(LessonExercise.ExerciseType.TARGET_WORD_INTEGRATION));
        }
        if (strategyMap.containsKey(LessonExercise.ExerciseType.ASSISTED_COMPOSITION)) {
            strategyMap.put(LessonExercise.ExerciseType.PARTIAL_OUTPUT,
                    strategyMap.get(LessonExercise.ExerciseType.ASSISTED_COMPOSITION));
        }

        log.info("Exercise evaluation service initialized with {} strategies", strategyMap.size());
    }

    /**
     * Evaluate a user's answer for an exercise
     *
     * @param exercise   The exercise being answered
     * @param userAnswer The user's submitted answer
     * @return Evaluation result with correctness, score, and feedback
     */
    public EvaluationResult evaluate(LessonExercise exercise, Object userAnswer) {
        LessonExercise.ExerciseType exerciseType = exercise.getExerciseType();
        ExerciseEvaluationStrategy strategy = strategyMap.get(exerciseType);

        if (strategy == null) {
            log.warn("No evaluation strategy found for exercise type: {}", exerciseType);
            return createFallbackResult(exercise, userAnswer);
        }

        int attemptNumber = (exercise.getAttemptCount() != null ? exercise.getAttemptCount() : 0) + 1;
        int maxAttempts = exercise.getMaxAttempts() != null
                ? exercise.getMaxAttempts()
                : strategy.getDefaultMaxAttempts();

        ExerciseData exerciseData = exercise.getExerciseData();

        try {
            EvaluationResult result = strategy.evaluate(exerciseData, userAnswer, attemptNumber, maxAttempts);
            log.debug("Evaluated exercise {} (type: {}): correct={}, score={}",
                    exercise.getId(), exerciseType, result.isCorrect(), result.getScore());
            return result;
        } catch (Exception e) {
            log.error("Error evaluating exercise {}: {}", exercise.getId(), e.getMessage(), e);
            return createErrorResult(e.getMessage());
        }
    }

    /**
     * Get the default max attempts for an exercise type
     */
    public int getDefaultMaxAttempts(LessonExercise.ExerciseType exerciseType) {
        ExerciseEvaluationStrategy strategy = strategyMap.get(exerciseType);
        return strategy != null ? strategy.getDefaultMaxAttempts() : 3;
    }

    /**
     * Check if an exercise type is supported
     */
    public boolean isSupported(LessonExercise.ExerciseType exerciseType) {
        return strategyMap.containsKey(exerciseType);
    }

    private EvaluationResult createFallbackResult(LessonExercise exercise, Object userAnswer) {
        // For unsupported types, mark as correct to avoid blocking progress
        return EvaluationResult.builder()
                .correct(true)
                .score(100)
                .feedbackMessage("Exercise completed.")
                .normalizedUserAnswer(userAnswer != null ? userAnswer.toString() : "")
                .build();
    }

    private EvaluationResult createErrorResult(String errorMessage) {
        return EvaluationResult.builder()
                .correct(false)
                .score(0)
                .feedbackMessage("An error occurred while evaluating your answer. Please try again.")
                .hint(errorMessage)
                .build();
    }
}
