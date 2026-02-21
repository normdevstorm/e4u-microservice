package com.e4u.lesson_service.services.impl;

import com.e4u.lesson_service.common.exception.ErrorCode;
import com.e4u.lesson_service.common.exception.ResourceNotFoundException;
import com.e4u.lesson_service.entities.DynamicLesson;
import com.e4u.lesson_service.entities.LessonExercise;
import com.e4u.lesson_service.entities.UserVocabInstance;
import com.e4u.lesson_service.mapper.LessonExerciseMapper;
import com.e4u.lesson_service.models.request.LessonExerciseCreateRequest;
import com.e4u.lesson_service.models.request.LessonExerciseFilterRequest;
import com.e4u.lesson_service.models.request.LessonExerciseUpdateRequest;
import com.e4u.lesson_service.models.response.LessonExerciseDetailResponse;
import com.e4u.lesson_service.models.response.LessonExerciseResponse;
import com.e4u.lesson_service.repositories.DynamicLessonRepository;
import com.e4u.lesson_service.repositories.LessonExerciseRepository;
import com.e4u.lesson_service.repositories.UserVocabInstanceRepository;
import com.e4u.lesson_service.repositories.specification.LessonExerciseSpecification;
import com.e4u.lesson_service.services.LessonExerciseService;
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
