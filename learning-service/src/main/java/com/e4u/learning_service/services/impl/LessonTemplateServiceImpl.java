package com.e4u.learning_service.services.impl;

import com.e4u.learning_service.dtos.request.LessonTemplateCreateRequest;
import com.e4u.learning_service.dtos.request.LessonTemplateUpdateRequest;
import com.e4u.learning_service.dtos.response.LessonTemplateDetailResponse;
import com.e4u.learning_service.dtos.response.LessonTemplateResponse;
import com.e4u.learning_service.entities.CurriculumUnit;
import com.e4u.learning_service.entities.LessonTemplate;
import com.e4u.learning_service.mapper.LessonTemplateMapper;
import com.e4u.learning_service.repositories.CurriculumUnitRepository;
import com.e4u.learning_service.repositories.LessonTemplateRepository;
import com.e4u.learning_service.services.LessonTemplateService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of LessonTemplateService.
 * Manages static lesson definitions within curriculum units.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LessonTemplateServiceImpl implements LessonTemplateService {

    private final LessonTemplateRepository lessonTemplateRepository;
    private final CurriculumUnitRepository curriculumUnitRepository;
    private final LessonTemplateMapper lessonTemplateMapper;

    @Override
    @Transactional
    public LessonTemplateResponse createLessonTemplate(LessonTemplateCreateRequest request) {
        log.info("Creating lesson template: {} for unit: {}", request.getLessonName(), request.getUnitId());

        CurriculumUnit unit = curriculumUnitRepository.findById(request.getUnitId())
                .orElseThrow(
                        () -> new EntityNotFoundException("CurriculumUnit not found with ID: " + request.getUnitId()));

        // Determine sequence order if not provided
        Integer sequenceOrder = request.getSequenceOrder();
        if (sequenceOrder == null) {
            List<LessonTemplate> existingLessons = lessonTemplateRepository
                    .findByUnitIdOrderBySequenceOrderAsc(request.getUnitId());
            sequenceOrder = existingLessons.isEmpty() ? 0
                    : existingLessons.get(existingLessons.size() - 1).getSequenceOrder() + 1;
        }

        LessonTemplate lessonTemplate = LessonTemplate.builder()
                .unit(unit)
                .lessonName(request.getLessonName())
                .lessonType(request.getLessonType())
                .sequenceOrder(sequenceOrder)
                .build();

        LessonTemplate saved = lessonTemplateRepository.save(lessonTemplate);
        log.info("Created lesson template with ID: {}", saved.getId());

        return lessonTemplateMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public LessonTemplateResponse getLessonTemplateById(UUID id) {
        log.debug("Fetching lesson template by ID: {}", id);

        LessonTemplate lessonTemplate = lessonTemplateRepository.findByIdWithUnit(id)
                .orElseThrow(() -> new EntityNotFoundException("LessonTemplate not found with ID: " + id));

        return lessonTemplateMapper.toResponse(lessonTemplate);
    }

    @Override
    @Transactional(readOnly = true)
    public LessonTemplateDetailResponse getLessonTemplateDetail(UUID id) {
        log.debug("Fetching lesson template detail by ID: {}", id);

        LessonTemplate lessonTemplate = lessonTemplateRepository.findByIdWithExercises(id)
                .orElseThrow(() -> new EntityNotFoundException("LessonTemplate not found with ID: " + id));

        LessonTemplateDetailResponse response = lessonTemplateMapper.toDetailResponse(lessonTemplate);
        // Set exercises with correct answers visible (admin view)
        if (lessonTemplate.getExerciseTemplates() != null) {
            response.setExercises(
                    lessonTemplate.getExerciseTemplates().stream()
                            .map(ex -> com.e4u.learning_service.dtos.response.ExerciseTemplateResponse.builder()
                                    .id(ex.getId())
                                    .lessonTemplateId(lessonTemplate.getId())
                                    .wordContextTemplateId(
                                            ex.getWordContextTemplate() != null ? ex.getWordContextTemplate().getId()
                                                    : null)
                                    .wordLemma(ex.getWordContextTemplate() != null
                                            && ex.getWordContextTemplate().getWord() != null
                                                    ? ex.getWordContextTemplate().getWord().getLemma()
                                                    : null)
                                    .exerciseType(ex.getExerciseType())
                                    .exerciseData(ex.getExerciseData())
                                    .createdForUserId(ex.getCreatedForUserId())
                                    .createdAt(ex.getCreatedAt())
                                    .updatedAt(ex.getUpdatedAt())
                                    .build())
                            .toList());
        }
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonTemplateResponse> getLessonTemplatesByUnit(UUID unitId) {
        log.debug("Fetching lesson templates for unit: {}", unitId);

        List<LessonTemplate> lessons = lessonTemplateRepository.findByUnitIdOrderBySequenceOrderAsc(unitId);
        lessons.forEach(arg0 -> lessonTemplateMapper.toResponse(arg0));
        return lessonTemplateMapper.toResponseList(lessons);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonTemplateResponse> getLessonTemplatesByUnitWithUserStatus(UUID unitId, UUID userId) {
        log.debug("Fetching lesson templates for unit: {} with user status for user: {}", unitId, userId);

        // TODO: Implement joining with UserLessonSession to include user progress
        // status
        List<LessonTemplate> lessons = lessonTemplateRepository.findByUnitIdOrderBySequenceOrderAsc(unitId);
        return lessonTemplateMapper.toResponseList(lessons);
    }

    @Override
    @Transactional
    public LessonTemplateResponse updateLessonTemplate(UUID id, LessonTemplateUpdateRequest request) {
        log.info("Updating lesson template: {}", id);

        LessonTemplate lessonTemplate = lessonTemplateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("LessonTemplate not found with ID: " + id));

        if (request.getLessonName() != null) {
            lessonTemplate.setLessonName(request.getLessonName());
        }
        if (request.getLessonType() != null) {
            lessonTemplate.setLessonType(request.getLessonType());
        }
        if (request.getSequenceOrder() != null) {
            lessonTemplate.setSequenceOrder(request.getSequenceOrder());
        }

        LessonTemplate saved = lessonTemplateRepository.save(lessonTemplate);
        log.info("Updated lesson template: {}", id);

        return lessonTemplateMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteLessonTemplate(UUID id) {
        log.info("Deleting lesson template: {}", id);

        if (!lessonTemplateRepository.existsById(id)) {
            throw new EntityNotFoundException("LessonTemplate not found with ID: " + id);
        }

        lessonTemplateRepository.deleteById(id);
        log.info("Deleted lesson template: {}", id);
    }

    @Override
    @Transactional
    public void reorderLessons(UUID unitId, List<UUID> lessonIds) {
        log.info("Reordering {} lessons in unit: {}", lessonIds.size(), unitId);

        for (int i = 0; i < lessonIds.size(); i++) {
            UUID lessonId = lessonIds.get(i);
            LessonTemplate lesson = lessonTemplateRepository.findById(lessonId)
                    .orElseThrow(() -> new EntityNotFoundException("LessonTemplate not found with ID: " + lessonId));

            if (!lesson.getUnit().getId().equals(unitId)) {
                throw new IllegalArgumentException("Lesson " + lessonId + " does not belong to unit " + unitId);
            }

            lesson.setSequenceOrder(i);
            lessonTemplateRepository.save(lesson);
        }

        log.info("Reordered lessons in unit: {}", unitId);
    }
}
