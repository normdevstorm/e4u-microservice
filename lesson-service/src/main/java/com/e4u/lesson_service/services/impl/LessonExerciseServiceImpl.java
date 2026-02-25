package com.e4u.lesson_service.services.impl;

import com.e4u.lesson_service.common.exception.ErrorCode;
import com.e4u.lesson_service.common.exception.ResourceNotFoundException;
import com.e4u.lesson_service.entities.DynamicLesson;
import com.e4u.lesson_service.entities.LessonExercise;
import com.e4u.lesson_service.entities.UserVocabInstance;
import com.e4u.lesson_service.mapper.LessonExerciseMapper;
import com.e4u.lesson_service.models.request.ExerciseSubmitRequest;
import com.e4u.lesson_service.models.request.LessonExerciseCreateRequest;
import com.e4u.lesson_service.models.request.LessonExerciseFilterRequest;
import com.e4u.lesson_service.models.request.LessonExerciseUpdateRequest;
import com.e4u.lesson_service.models.response.ExerciseSubmitResponse;
import com.e4u.lesson_service.models.response.LessonExerciseDetailResponse;
import com.e4u.lesson_service.models.response.LessonExerciseResponse;
import com.e4u.lesson_service.repositories.DynamicLessonRepository;
import com.e4u.lesson_service.repositories.LessonExerciseRepository;
import com.e4u.lesson_service.repositories.UserVocabInstanceRepository;
import com.e4u.lesson_service.repositories.specification.LessonExerciseSpecification;
import com.e4u.lesson_service.services.LessonExerciseService;
import com.e4u.lesson_service.services.evaluation.EvaluationResult;
import com.e4u.lesson_service.services.evaluation.ExerciseEvaluationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of LessonExerciseService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LessonExerciseServiceImpl implements LessonExerciseService {

    private final LessonExerciseRepository lessonExerciseRepository;
    private final DynamicLessonRepository dynamicLessonRepository;
    private final UserVocabInstanceRepository userVocabInstanceRepository;
    private final LessonExerciseMapper mapper;
    private final ExerciseEvaluationService evaluationService;

    @Override
    @Transactional
    public ExerciseSubmitResponse submitAnswer(UUID exerciseId, ExerciseSubmitRequest request) {
        log.info("Submitting answer for exercise: {}", exerciseId);

        // 1. Find the exercise
        LessonExercise exercise = findByIdOrThrow(exerciseId);

        // 2. Check if already completed
        if (Boolean.TRUE.equals(exercise.getIsCompleted())) {
            log.warn("Exercise {} is already completed", exerciseId);
            return buildAlreadyCompletedResponse(exercise);
        }

        // 3. Evaluate the answer
        EvaluationResult evaluation = evaluationService.evaluate(exercise, request.getAnswer());

        // 4. Update exercise state
        int currentAttempt = (exercise.getAttemptCount() != null ? exercise.getAttemptCount() : 0) + 1;
        int maxAttempts = exercise.getMaxAttempts() != null
                ? exercise.getMaxAttempts()
                : evaluationService.getDefaultMaxAttempts(exercise.getExerciseType());

        exercise.setAttemptCount(currentAttempt);
        exercise.setUserAnswer(evaluation.getNormalizedUserAnswer() != null
                ? evaluation.getNormalizedUserAnswer()
                : String.valueOf(request.getAnswer()));

        if (request.getTimeSpentSeconds() != null) {
            int totalTime = (exercise.getTimeSpentSeconds() != null ? exercise.getTimeSpentSeconds() : 0)
                    + request.getTimeSpentSeconds();
            exercise.setTimeSpentSeconds(totalTime);
        }

        boolean isLastAttempt = currentAttempt >= maxAttempts;
        boolean shouldComplete = evaluation.isCorrect() || isLastAttempt;

        if (shouldComplete) {
            exercise.setIsCompleted(true);
            exercise.setIsCorrect(evaluation.isCorrect());
            exercise.setScore(evaluation.getScore());
        }

        exercise = lessonExerciseRepository.save(exercise);

        // 5. Update lesson progress if exercise is now complete
        DynamicLesson lesson = exercise.getLesson();
        if (shouldComplete && lesson != null) {
            updateLessonProgress(lesson, evaluation.isCorrect());
        }

        // 6. Build and return response
        return buildSubmitResponse(exercise, evaluation, currentAttempt, maxAttempts, shouldComplete);
    }

    /**
     * Update lesson progress after exercise completion
     */
    private void updateLessonProgress(DynamicLesson lesson, boolean isCorrect) {
        // Start lesson if not started
        if (lesson.getStatus() == DynamicLesson.LessonStatus.NOT_STARTED) {
            lesson.startLesson();
        }

        // Increment progress
        lesson.incrementProgress(isCorrect);

        // Check if lesson is complete
        if (lesson.getCompletedItems() != null && lesson.getTotalItems() != null
                && lesson.getCompletedItems() >= lesson.getTotalItems()) {
            lesson.completeLesson(lesson.getAccuracyRate());
            log.info("Lesson {} completed with accuracy: {}", lesson.getId(), lesson.getAccuracyRate());
        }

        dynamicLessonRepository.save(lesson);
    }

    /**
     * Build response for already completed exercise
     */
    private ExerciseSubmitResponse buildAlreadyCompletedResponse(LessonExercise exercise) {
        return ExerciseSubmitResponse.builder()
                .exerciseId(exercise.getId())
                .isCorrect(exercise.getIsCorrect())
                .score(exercise.getScore())
                .attemptNumber(exercise.getAttemptCount())
                .maxAttempts(exercise.getMaxAttempts())
                .feedback(ExerciseSubmitResponse.ExerciseFeedback.builder()
                        .message("This exercise has already been completed.")
                        .userAnswer(exercise.getUserAnswer())
                        .build())
                .exerciseState(ExerciseSubmitResponse.ExerciseState.builder()
                        .isCompleted(true)
                        .canRetry(false)
                        .attemptsRemaining(0)
                        .build())
                .lessonProgress(buildLessonProgress(exercise.getLesson()))
                .build();
    }

    /**
     * Build the submit response from evaluation result
     */
    private ExerciseSubmitResponse buildSubmitResponse(
            LessonExercise exercise,
            EvaluationResult evaluation,
            int attemptNumber,
            int maxAttempts,
            boolean isCompleted) {

        int attemptsRemaining = Math.max(0, maxAttempts - attemptNumber);
        boolean canRetry = !isCompleted && attemptsRemaining > 0;

        // Build feedback - hide correct answer unless it's the last attempt and wrong
        ExerciseSubmitResponse.ExerciseFeedback feedback = ExerciseSubmitResponse.ExerciseFeedback.builder()
                .message(evaluation.getFeedbackMessage())
                .userAnswer(evaluation.getNormalizedUserAnswer())
                .hint(evaluation.getHint())
                .explanation(evaluation.getExplanation())
                .alternatives(evaluation.getAlternatives())
                .grammarNotes(evaluation.getGrammarNotes())
                .build();

        // Only reveal correct answer on last failed attempt
        if (!evaluation.isCorrect() && !canRetry) {
            feedback.setCorrectAnswer(evaluation.getCorrectAnswer());
        }

        return ExerciseSubmitResponse.builder()
                .exerciseId(exercise.getId())
                .isCorrect(evaluation.isCorrect())
                .score(evaluation.getScore())
                .attemptNumber(attemptNumber)
                .maxAttempts(maxAttempts)
                .feedback(feedback)
                .exerciseState(ExerciseSubmitResponse.ExerciseState.builder()
                        .isCompleted(isCompleted)
                        .canRetry(canRetry)
                        .attemptsRemaining(attemptsRemaining)
                        .build())
                .lessonProgress(buildLessonProgress(exercise.getLesson()))
                .vocabProgress(buildVocabProgress(exercise.getWordInstance()))
                .build();
    }

    /**
     * Build lesson progress summary
     */
    private ExerciseSubmitResponse.LessonProgress buildLessonProgress(DynamicLesson lesson) {
        if (lesson == null) {
            return null;
        }

        return ExerciseSubmitResponse.LessonProgress.builder()
                .lessonId(lesson.getId())
                .completedItems(lesson.getCompletedItems())
                .totalItems(lesson.getTotalItems())
                .correctItems(lesson.getCorrectItems())
                .accuracyRate(lesson.getAccuracyRate())
                .build();
    }

    /**
     * Build vocab progress summary
     */
    private ExerciseSubmitResponse.VocabProgress buildVocabProgress(UserVocabInstance vocab) {
        if (vocab == null) {
            return null;
        }

        return ExerciseSubmitResponse.VocabProgress.builder()
                .wordId(vocab.getId())
                .wordText(vocab.getWordText())
                .isLearning(vocab.getIsLearning())
                .isMastered(vocab.getIsMastered())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LessonExerciseResponse> getAll(int page, int size, String sortBy, String sortDirection) {
        log.debug("Fetching all lesson exercises - page: {}, size: {}", page, size);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return lessonExerciseRepository.findByDeletedFalse(pageable)
                .map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public LessonExerciseResponse getById(UUID id) {
        log.debug("Fetching lesson exercise by id: {}", id);

        LessonExercise entity = findByIdOrThrow(id);
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public LessonExerciseDetailResponse getByIdWithDetails(UUID id) {
        log.debug("Fetching lesson exercise with details by id: {}", id);

        LessonExercise entity = findByIdOrThrow(id);
        return mapper.toDetailResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonExerciseResponse> getByVocabId(UUID wordInstanceId) {
        log.debug("Fetching exercises for vocab instance: {}", wordInstanceId);

        List<LessonExercise> entities = lessonExerciseRepository.findByWordInstanceIdAndDeletedFalse(wordInstanceId);
        return mapper.toResponseList(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LessonExerciseResponse> getByVocabId(UUID wordInstanceId, int page, int size, String sortBy,
            String sortDirection) {
        log.debug("Fetching paginated exercises for vocab instance: {}", wordInstanceId);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return lessonExerciseRepository.findByWordInstanceIdAndDeletedFalse(wordInstanceId, pageable)
                .map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonExerciseResponse> getByLessonId(UUID lessonId) {
        log.debug("Fetching exercises for lesson: {}", lessonId);

        List<LessonExercise> entities = lessonExerciseRepository
                .findByLessonIdAndDeletedFalseOrderBySequenceOrderAsc(lessonId);
        return mapper.toResponseList(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LessonExerciseResponse> getByLessonId(UUID lessonId, int page, int size, String sortBy,
            String sortDirection) {
        log.debug("Fetching paginated exercises for lesson: {}", lessonId);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return lessonExerciseRepository.findByLessonIdAndDeletedFalse(lessonId, pageable)
                .map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonExerciseResponse> getByUnitId(UUID unitId) {
        log.debug("Fetching exercises for unit: {}", unitId);

        List<LessonExercise> entities = lessonExerciseRepository.findByUnitId(unitId);
        return mapper.toResponseList(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LessonExerciseResponse> getByUnitId(UUID unitId, int page, int size, String sortBy,
            String sortDirection) {
        log.debug("Fetching paginated exercises for unit: {}", unitId);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return lessonExerciseRepository.findByUnitId(unitId, pageable)
                .map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LessonExerciseResponse> filter(LessonExerciseFilterRequest filterRequest) {
        log.debug("Filtering lesson exercises with criteria: {}", filterRequest);

        Sort sort = Sort.by(
                Sort.Direction.fromString(filterRequest.getSortDirection()),
                filterRequest.getSortBy());
        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize(), sort);

        return lessonExerciseRepository.findAll(LessonExerciseSpecification.withFilter(filterRequest), pageable)
                .map(mapper::toResponse);
    }

    @Override
    @Transactional
    public List<LessonExerciseResponse> createBatch(List<LessonExerciseCreateRequest> requests) {
        log.info("Creating batch of {} lesson exercises", requests.size());

        List<LessonExercise> entities = new ArrayList<>();

        for (LessonExerciseCreateRequest request : requests) {
            LessonExercise entity = mapper.toEntity(request);

            // Set lesson relationship
            DynamicLesson lesson = dynamicLessonRepository.findByIdAndDeletedFalse(request.getLessonId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.LESSON_NOT_FOUND,
                            "Dynamic lesson not found with id: " + request.getLessonId()));
            entity.setLesson(lesson);

            // Set word instance relationship
            UserVocabInstance wordInstance = userVocabInstanceRepository
                    .findByIdAndDeletedFalse(request.getWordInstanceId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.VOCAB_INSTANCE_NOT_FOUND,
                            "Vocabulary instance not found with id: " + request.getWordInstanceId()));
            entity.setWordInstance(wordInstance);

            entities.add(entity);
        }

        entities = lessonExerciseRepository.saveAll(entities);

        log.info("Created {} lesson exercises", entities.size());
        return mapper.toResponseList(entities);
    }

    @Override
    @Transactional
    public LessonExerciseResponse partialUpdate(UUID id, LessonExerciseUpdateRequest request) {
        log.info("Partially updating lesson exercise: {}", id);

        LessonExercise entity = findByIdOrThrow(id);

        // Update lesson if provided
        if (request.getLessonId() != null) {
            DynamicLesson lesson = dynamicLessonRepository.findByIdAndDeletedFalse(request.getLessonId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.LESSON_NOT_FOUND,
                            "Dynamic lesson not found with id: " + request.getLessonId()));
            entity.setLesson(lesson);
        }

        // Update word instance if provided
        if (request.getWordInstanceId() != null) {
            UserVocabInstance wordInstance = userVocabInstanceRepository
                    .findByIdAndDeletedFalse(request.getWordInstanceId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.VOCAB_INSTANCE_NOT_FOUND,
                            "Vocabulary instance not found with id: " + request.getWordInstanceId()));
            entity.setWordInstance(wordInstance);
        }

        // Apply other partial updates via mapper
        mapper.partialUpdate(entity, request);

        entity = lessonExerciseRepository.save(entity);

        log.info("Updated lesson exercise: {}", id);
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void softDelete(UUID id) {
        log.info("Soft deleting lesson exercise: {}", id);

        // Verify existence
        if (!lessonExerciseRepository.existsById(id)) {
            throw new ResourceNotFoundException(ErrorCode.LESSON_EXERCISE_NOT_FOUND,
                    "Lesson exercise not found with id: " + id);
        }

        int updated = lessonExerciseRepository.softDeleteById(id, Instant.now());
        if (updated == 0) {
            log.warn("No lesson exercise was deleted with id: {}", id);
        } else {
            log.info("Soft deleted lesson exercise: {}", id);
        }
    }

    @Override
    @Transactional
    public void softDeleteBatch(List<UUID> ids) {
        log.info("Soft deleting {} lesson exercises", ids.size());

        int updated = lessonExerciseRepository.softDeleteByIds(ids, Instant.now());
        log.info("Soft deleted {} lesson exercises", updated);
    }

    /**
     * Helper method to find exercise by ID or throw exception
     */
    private LessonExercise findByIdOrThrow(UUID id) {
        return lessonExerciseRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.LESSON_EXERCISE_NOT_FOUND,
                        "Lesson exercise not found with id: " + id));
    }
}
