package com.e4u.learning_service.services.impl;

import com.e4u.learning_service.dtos.request.UserExerciseAttemptRequest;
import com.e4u.learning_service.dtos.response.UserExerciseAttemptResponse;
import com.e4u.learning_service.entities.ExerciseTemplate;
import com.e4u.learning_service.entities.UserExerciseAttempt;
import com.e4u.learning_service.entities.UserLessonSession;
import com.e4u.learning_service.mapper.UserExerciseAttemptMapper;
import com.e4u.learning_service.repositories.ExerciseTemplateRepository;
import com.e4u.learning_service.repositories.UserExerciseAttemptRepository;
import com.e4u.learning_service.repositories.UserLessonSessionRepository;
import com.e4u.learning_service.services.UserExerciseAttemptService;
import com.e4u.learning_service.services.UserVocabProgressService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of UserExerciseAttemptService.
 * Handles answer submission, validation, and SRS updates.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserExerciseAttemptServiceImpl implements UserExerciseAttemptService {

    private final UserExerciseAttemptRepository attemptRepository;
    private final UserLessonSessionRepository sessionRepository;
    private final ExerciseTemplateRepository exerciseTemplateRepository;
    private final UserExerciseAttemptMapper attemptMapper;
    private final UserVocabProgressService vocabProgressService;

    @Override
    @Transactional
    public UserExerciseAttemptResponse submitAttempt(UserExerciseAttemptRequest request) {
        log.info("Submitting exercise attempt for session: {} exercise: {}", 
                request.getSessionId(), request.getExerciseTemplateId());

        UserLessonSession session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Session not found with ID: " + request.getSessionId()));

        ExerciseTemplate exerciseTemplate = exerciseTemplateRepository.findById(request.getExerciseTemplateId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "ExerciseTemplate not found with ID: " + request.getExerciseTemplateId()));

        // Validate the answer
        boolean isCorrect = validateAnswer(exerciseTemplate, request.getUserAnswer());

        // Create attempt record
        UserExerciseAttempt attempt = UserExerciseAttempt.builder()
                .session(session)
                .exerciseTemplate(exerciseTemplate)
                .userAnswer(request.getUserAnswer())
                .isCorrect(isCorrect)
                .timeTakenSeconds(request.getTimeTakenSeconds())
                .build();

        attempt = attemptRepository.save(attempt);

        // Update session progress
        session.recordExerciseCompletion(isCorrect);
        sessionRepository.save(session);

        // Update vocab progress if the exercise targets a word via WordContextTemplate
        if (exerciseTemplate.getWordContextTemplate() != null 
                && exerciseTemplate.getWordContextTemplate().getWord() != null) {
            UUID wordId = exerciseTemplate.getWordContextTemplate().getWord().getId();
            UUID userId = session.getUserId();
            
            if (isCorrect) {
                vocabProgressService.recordCorrectAnswer(userId, wordId);
            } else {
                vocabProgressService.recordIncorrectAnswer(userId, wordId);
            }
        }

        log.info("Attempt recorded: {} isCorrect: {}", attempt.getId(), isCorrect);

        // Return response with correct answer revealed
        return attemptMapper.toResponseWithAnswer(attempt);
    }

    @Override
    @Transactional(readOnly = true)
    public UserExerciseAttemptResponse getAttemptById(UUID attemptId) {
        log.debug("Fetching attempt by ID: {}", attemptId);

        UserExerciseAttempt attempt = attemptRepository.findByIdWithDetails(attemptId)
                .orElseThrow(() -> new EntityNotFoundException("Attempt not found with ID: " + attemptId));

        return attemptMapper.toResponseWithAnswer(attempt);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserExerciseAttemptResponse> getAttemptsBySession(UUID sessionId) {
        log.debug("Fetching attempts for session: {}", sessionId);

        List<UserExerciseAttempt> attempts = attemptRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);
        return attemptMapper.toResponseList(attempts);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserExerciseAttemptResponse> getAttemptsBySessionAndExercise(UUID sessionId, UUID exerciseTemplateId) {
        log.debug("Fetching attempts for session: {} exercise: {}", sessionId, exerciseTemplateId);

        // For now, return single attempt (could be multiple if retry is allowed)
        return attemptRepository.findBySessionIdAndExerciseTemplateId(sessionId, exerciseTemplateId)
                .map(attemptMapper::toResponseWithAnswer)
                .map(List::of)
                .orElse(List.of());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserExerciseAttemptResponse> getAttemptsByUser(UUID userId) {
        log.debug("Fetching all attempts for user: {}", userId);

        List<UserExerciseAttempt> attempts = attemptRepository.findByUserId(userId);
        return attemptMapper.toResponseList(attempts);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserExerciseAttemptResponse> getRecentAttemptsByUser(UUID userId, int limit) {
        log.debug("Fetching recent {} attempts for user: {}", limit, userId);

        List<UserExerciseAttempt> attempts = attemptRepository.findRecentByUserId(userId, PageRequest.of(0, limit));
        return attemptMapper.toResponseList(attempts);
    }

    @Override
    @Transactional(readOnly = true)
    public float calculateSessionAccuracy(UUID sessionId) {
        log.debug("Calculating accuracy for session: {}", sessionId);

        Long total = attemptRepository.countBySessionId(sessionId);
        Long correct = attemptRepository.countCorrectBySessionId(sessionId);

        if (total == null || total == 0) {
            return 0.0f;
        }

        return (correct != null ? correct.floatValue() : 0f) / total.floatValue();
    }

    @Override
    @Transactional(readOnly = true)
    public float calculateUserAccuracy(UUID userId) {
        log.debug("Calculating overall accuracy for user: {}", userId);

        Long total = attemptRepository.countByUserId(userId);
        Long correct = attemptRepository.countCorrectByUserId(userId);

        if (total == null || total == 0) {
            return 0.0f;
        }

        return (correct != null ? correct.floatValue() : 0f) / total.floatValue();
    }

    @Override
    @Transactional
    public void deleteAttempt(UUID attemptId) {
        log.info("Deleting attempt: {}", attemptId);

        if (!attemptRepository.existsById(attemptId)) {
            throw new EntityNotFoundException("Attempt not found with ID: " + attemptId);
        }

        attemptRepository.deleteById(attemptId);
        log.info("Deleted attempt: {}", attemptId);
    }

    // ========== Private Helper Methods ==========

    /**
     * Validate user's answer against the exercise data.
     * TODO: This should be refactored to use ExerciseEvaluator strategies
     * for proper type-safe validation based on ExerciseData type.
     */
    private boolean validateAnswer(ExerciseTemplate exercise, Map<String, Object> userAnswer) {
        if (userAnswer == null || exercise.getExerciseData() == null) {
            return false;
        }

        // Basic validation - compare user answer with embedded correctAnswer in ExerciseData
        // For proper validation, use ExerciseEvaluator strategies
        Object userValue = userAnswer.get("answer");
        if (userValue == null) {
            userValue = userAnswer.get("value");
        }
        
        // TODO: Implement proper validation using ExerciseData subclass methods
        // For now, delegate to the evaluator service for real validation
        return false;
    }
}
