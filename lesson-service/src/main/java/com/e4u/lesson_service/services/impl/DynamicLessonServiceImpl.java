package com.e4u.lesson_service.services.impl;

import com.e4u.lesson_service.common.exception.ErrorCode;
import com.e4u.lesson_service.common.exception.ResourceNotFoundException;
import com.e4u.lesson_service.entities.DynamicLesson;
import com.e4u.lesson_service.entities.LessonItem;
import com.e4u.lesson_service.entities.LessonItemKey;
import com.e4u.lesson_service.entities.UserUnitState;
import com.e4u.lesson_service.entities.UserVocabInstance;
import com.e4u.lesson_service.mapper.DynamicLessonMapper;
import com.e4u.lesson_service.mapper.UserVocabInstanceMapper;
import com.e4u.lesson_service.models.request.DynamicLessonCreateRequest;
import com.e4u.lesson_service.models.request.DynamicLessonFilterRequest;
import com.e4u.lesson_service.models.request.DynamicLessonUpdateRequest;
import com.e4u.lesson_service.models.response.DynamicLessonDetailResponse;
import com.e4u.lesson_service.models.response.DynamicLessonResponse;
import com.e4u.lesson_service.models.response.UserVocabInstanceResponse;
import com.e4u.lesson_service.repositories.DynamicLessonRepository;
import com.e4u.lesson_service.repositories.LessonItemRepository;
import com.e4u.lesson_service.repositories.UserUnitStateRepository;
import com.e4u.lesson_service.repositories.UserVocabInstanceRepository;
import com.e4u.lesson_service.repositories.specification.DynamicLessonSpecification;
import com.e4u.lesson_service.services.DynamicLessonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Implementation of DynamicLessonService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DynamicLessonServiceImpl implements DynamicLessonService {

    private final DynamicLessonRepository dynamicLessonRepository;
    private final LessonItemRepository lessonItemRepository;
    private final UserVocabInstanceRepository userVocabInstanceRepository;
    private final UserUnitStateRepository userUnitStateRepository;
    private final DynamicLessonMapper mapper;
    private final UserVocabInstanceMapper vocabMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<DynamicLessonResponse> getAll(int page, int size, String sortBy, String sortDirection) {
        log.debug("Fetching all dynamic lessons - page: {}, size: {}", page, size);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return dynamicLessonRepository.findByDeletedFalse(pageable)
                .map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public DynamicLessonResponse getById(UUID id) {
        log.debug("Fetching dynamic lesson by id: {}", id);

        DynamicLesson entity = findByIdOrThrow(id);
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public DynamicLessonDetailResponse getByIdWithDetails(UUID id) {
        log.debug("Fetching dynamic lesson with details by id: {}", id);

        DynamicLesson entity = dynamicLessonRepository.findByIdWithLessonItems(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.LESSON_NOT_FOUND,
                        "Dynamic lesson not found with id: " + id));

        return mapper.toDetailResponse(entity, vocabMapper);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DynamicLessonResponse> getByUserId(UUID userId) {
        log.debug("Fetching dynamic lessons for user: {}", userId);

        List<DynamicLesson> entities = dynamicLessonRepository.findByUserIdAndDeletedFalse(userId);
        return mapper.toResponseList(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DynamicLessonResponse> getByUserId(UUID userId, int page, int size, String sortBy,
            String sortDirection) {
        log.debug("Fetching paginated dynamic lessons for user: {}", userId);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return dynamicLessonRepository.findByUserIdAndDeletedFalse(userId, pageable)
                .map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DynamicLessonResponse> getByUnitId(UUID userUnitStateId) {
        log.debug("Fetching dynamic lessons for unit: {}", userUnitStateId);

        List<DynamicLesson> entities = dynamicLessonRepository.findByUserUnitStateIdAndDeletedFalse(userUnitStateId);
        return mapper.toResponseList(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DynamicLessonResponse> getByUnitId(UUID userUnitStateId, int page, int size, String sortBy,
            String sortDirection) {
        log.debug("Fetching paginated dynamic lessons for unit: {}", userUnitStateId);

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return dynamicLessonRepository.findByUserUnitStateIdAndDeletedFalse(userUnitStateId, pageable)
                .map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DynamicLessonResponse> filter(DynamicLessonFilterRequest filterRequest) {
        log.debug("Filtering dynamic lessons with criteria: {}", filterRequest);

        Sort sort = Sort.by(
                Sort.Direction.fromString(filterRequest.getSortDirection()),
                filterRequest.getSortBy());
        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize(), sort);

        return dynamicLessonRepository.findAll(DynamicLessonSpecification.withFilter(filterRequest), pageable)
                .map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserVocabInstanceResponse> getAllVocabByDynamicLesson(UUID lessonId) {
        log.debug("Fetching all vocab instances for lesson: {}", lessonId);

        // Verify lesson exists
        findByIdOrThrow(lessonId);

        List<LessonItem> lessonItems = lessonItemRepository.findByLessonIdWithVocabInstances(lessonId);

        return lessonItems.stream()
                .map(item -> vocabMapper.toResponse(item.getWordInstance()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DynamicLessonResponse create(DynamicLessonCreateRequest request) {
        log.info("Creating new dynamic lesson for user: {} with {} vocab items",
                request.getUserId(), request.getVocabInstanceIds().size());

        // Build the lesson entity
        DynamicLesson lesson = DynamicLesson.builder()
                .userId(request.getUserId())
                .status(DynamicLesson.LessonStatus.NOT_STARTED)
                .totalItems(request.getVocabInstanceIds().size())
                .completedItems(0)
                .lessonItems(new HashSet<>())
                .build();

        // Set UserUnitState if provided
        if (request.getUserUnitStateId() != null) {
            UserUnitState unitState = userUnitStateRepository.findById(request.getUserUnitStateId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "UserUnitState not found with id: " + request.getUserUnitStateId()));
            lesson.setUserUnitState(unitState);
        }

        // Save the lesson first to get the ID
        lesson = dynamicLessonRepository.save(lesson);

        // Create LessonItems for each vocab instance
        AtomicInteger sequenceOrder = new AtomicInteger(1);
        final DynamicLesson savedLesson = lesson;

        Set<LessonItem> lessonItems = new HashSet<>();
        for (UUID vocabId : request.getVocabInstanceIds()) {
            UserVocabInstance vocabInstance = userVocabInstanceRepository.findByIdAndDeletedFalse(vocabId)
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.VOCAB_INSTANCE_NOT_FOUND,
                            "Vocabulary instance not found with id: " + vocabId));

            LessonItemKey key = LessonItemKey.builder()
                    .lessonId(savedLesson.getId())
                    .wordInstanceId(vocabId)
                    .build();

            LessonItem lessonItem = LessonItem.builder()
                    .id(key)
                    .lesson(savedLesson)
                    .wordInstance(vocabInstance)
                    .isMasteredInLesson(false)
                    .sequenceOrder(sequenceOrder.getAndIncrement())
                    .build();

            lessonItems.add(lessonItem);
        }

        // Save all lesson items
        lessonItemRepository.saveAll(lessonItems);
        lesson.setLessonItems(lessonItems);

        log.info("Created dynamic lesson with id: {}", lesson.getId());
        return mapper.toResponse(lesson);
    }

    @Override
    @Transactional
    public DynamicLessonResponse partialUpdate(UUID id, DynamicLessonUpdateRequest request) {
        log.info("Partially updating dynamic lesson: {}", id);

        DynamicLesson lesson = findByIdOrThrow(id);

        // Update UserUnitState if provided
        if (request.getUserUnitStateId() != null) {
            UserUnitState unitState = userUnitStateRepository.findById(request.getUserUnitStateId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "UserUnitState not found with id: " + request.getUserUnitStateId()));
            lesson.setUserUnitState(unitState);
        }

        // Update status if provided
        if (request.getStatus() != null) {
            lesson.setStatus(request.getStatus());

            // Auto-set timestamps based on status
            if (request.getStatus() == DynamicLesson.LessonStatus.IN_PROGRESS && lesson.getStartedAt() == null) {
                lesson.setStartedAt(LocalDateTime.now());
            } else if (request.getStatus() == DynamicLesson.LessonStatus.COMPLETED && lesson.getCompletedAt() == null) {
                lesson.setCompletedAt(LocalDateTime.now());
            }
        }

        // Update accuracy rate if provided
        if (request.getAccuracyRate() != null) {
            lesson.setAccuracyRate(request.getAccuracyRate());
        }

        // Update completed items if provided
        if (request.getCompletedItems() != null) {
            lesson.setCompletedItems(request.getCompletedItems());
        }

        // Add new vocab instances if provided
        if (request.getAddVocabInstanceIds() != null && !request.getAddVocabInstanceIds().isEmpty()) {
            int currentMaxOrder = lesson.getLessonItems().stream()
                    .mapToInt(item -> item.getSequenceOrder() != null ? item.getSequenceOrder() : 0)
                    .max()
                    .orElse(0);

            AtomicInteger sequenceOrder = new AtomicInteger(currentMaxOrder + 1);

            for (UUID vocabId : request.getAddVocabInstanceIds()) {
                // Check if already exists
                if (!lessonItemRepository.existsByIdLessonIdAndIdWordInstanceId(id, vocabId)) {
                    UserVocabInstance vocabInstance = userVocabInstanceRepository.findByIdAndDeletedFalse(vocabId)
                            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.VOCAB_INSTANCE_NOT_FOUND,
                                    "Vocabulary instance not found with id: " + vocabId));

                    LessonItemKey key = LessonItemKey.builder()
                            .lessonId(id)
                            .wordInstanceId(vocabId)
                            .build();

                    LessonItem lessonItem = LessonItem.builder()
                            .id(key)
                            .lesson(lesson)
                            .wordInstance(vocabInstance)
                            .isMasteredInLesson(false)
                            .sequenceOrder(sequenceOrder.getAndIncrement())
                            .build();

                    lessonItemRepository.save(lessonItem);
                    lesson.getLessonItems().add(lessonItem);
                }
            }
        }

        // Remove vocab instances if provided
        if (request.getRemoveVocabInstanceIds() != null && !request.getRemoveVocabInstanceIds().isEmpty()) {
            for (UUID vocabId : request.getRemoveVocabInstanceIds()) {
                LessonItemKey key = LessonItemKey.builder()
                        .lessonId(id)
                        .wordInstanceId(vocabId)
                        .build();

                lessonItemRepository.deleteById(key);
                lesson.getLessonItems().removeIf(item -> item.getId().getWordInstanceId().equals(vocabId));
            }
        }

        // Update total items count
        lesson.setTotalItems(lesson.getLessonItems().size());

        lesson = dynamicLessonRepository.save(lesson);

        log.info("Updated dynamic lesson: {}", id);
        return mapper.toResponse(lesson);
    }

    @Override
    @Transactional
    public void softDelete(UUID id) {
        log.info("Soft deleting dynamic lesson: {}", id);

        // Verify existence
        if (!dynamicLessonRepository.existsById(id)) {
            throw new ResourceNotFoundException(ErrorCode.LESSON_NOT_FOUND,
                    "Dynamic lesson not found with id: " + id);
        }

        int updated = dynamicLessonRepository.softDeleteById(id, Instant.now());
        if (updated == 0) {
            log.warn("No dynamic lesson was deleted with id: {}", id);
        } else {
            log.info("Soft deleted dynamic lesson: {}", id);
        }
    }

    /**
     * Helper method to find lesson by ID or throw exception
     */
    private DynamicLesson findByIdOrThrow(UUID id) {
        return dynamicLessonRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.LESSON_NOT_FOUND,
                        "Dynamic lesson not found with id: " + id));
    }
}
