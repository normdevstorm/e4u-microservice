package com.e4u.learning_service.services.impl;

import com.e4u.learning_service.common.exception.ErrorCode;
import com.e4u.learning_service.common.exception.ResourceNotFoundException;
import com.e4u.learning_service.dtos.request.CurriculumCreateRequest;
import com.e4u.learning_service.dtos.request.CurriculumFilterRequest;
import com.e4u.learning_service.dtos.request.CurriculumUpdateRequest;
import com.e4u.learning_service.dtos.response.CurriculumDetailResponse;
import com.e4u.learning_service.dtos.response.CurriculumResponse;
import com.e4u.learning_service.entities.Curriculum;
import com.e4u.learning_service.entities.GoalDefinition;
import com.e4u.learning_service.mapper.CurriculumMapper;
import com.e4u.learning_service.repositories.CurriculumRepository;
import com.e4u.learning_service.repositories.GoalDefinitionRepository;
import com.e4u.learning_service.repositories.UserGoalRepository;
import com.e4u.learning_service.repositories.specification.CurriculumSpecification;
import com.e4u.learning_service.services.CurriculumService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service implementation for Curriculum operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CurriculumServiceImpl implements CurriculumService {

    private final CurriculumRepository repository;
    private final GoalDefinitionRepository goalDefinitionRepository;
    private final UserGoalRepository userGoalRepository;
    private final CurriculumMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<CurriculumResponse> getAll(int page, int size, String sortBy, String sortDirection) {
        log.debug("Fetching all curricula - page: {}, size: {}", page, size);
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return repository.findByDeletedFalse(pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CurriculumResponse getById(UUID curriculumId) {
        log.debug("Fetching curriculum by id: {}", curriculumId);
        Curriculum entity = findByIdOrThrow(curriculumId);
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public CurriculumDetailResponse getByIdWithDetails(UUID curriculumId) {
        log.debug("Fetching curriculum with details by id: {}", curriculumId);
        Curriculum entity = repository.findByIdWithUnits(curriculumId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CURRICULUM_NOT_FOUND,
                        "Curriculum not found with id: " + curriculumId));
        return mapper.toDetailResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CurriculumResponse> filter(CurriculumFilterRequest filterRequest) {
        log.debug("Filtering curricula with request: {}", filterRequest);
        Sort sort = Sort.by(Sort.Direction.fromString(filterRequest.getSortDirection()), filterRequest.getSortBy());
        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize(), sort);
        return repository.findAll(CurriculumSpecification.withFilter(filterRequest), pageable)
                .map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CurriculumResponse> getByGoalId(UUID goalId) {
        log.debug("Fetching curricula by goal id: {}", goalId);
        return repository.findByGoalDefinition_IdAndDeletedFalse(goalId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CurriculumResponse> getByUser(UUID userId) {
        log.debug("Fetching curricula for user: {}", userId);
        // Fetch the user's active goal IDs
        List<UUID> goalIds = userGoalRepository.findByUserIdAndDeletedFalse(userId)
                .stream()
                .map(ug -> ug.getGoalDefinition().getId())
                .distinct()
                .collect(Collectors.toList());
        log.debug("User {} has {} goals: {}", userId, goalIds.size(), goalIds);
        if (goalIds.isEmpty()) {
            return List.of();
        }
        return repository.findByGoalDefinition_IdInAndDeletedFalse(goalIds)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CurriculumResponse create(CurriculumCreateRequest request) {
        log.info("Creating new curriculum: {}", request.getCurriculumName());

        Curriculum entity = mapper.toEntity(request);

        // Set goal definition if provided
        if (request.getGoalId() != null) {
            GoalDefinition goalDefinition = goalDefinitionRepository.findByIdAndDeletedFalse(request.getGoalId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.GOAL_NOT_FOUND,
                            "Goal definition not found with id: " + request.getGoalId()));
            entity.setGoalDefinition(goalDefinition);
        }

        entity = repository.save(entity);
        log.info("Created curriculum with id: {}", entity.getId());
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public List<CurriculumResponse> createBatch(List<CurriculumCreateRequest> requests) {
        log.info("Creating {} curricula in batch", requests.size());
        List<Curriculum> entities = requests.stream()
                .map(request -> {
                    Curriculum entity = mapper.toEntity(request);
                    if (request.getGoalId() != null) {
                        GoalDefinition goalDefinition = goalDefinitionRepository
                                .findByIdAndDeletedFalse(request.getGoalId())
                                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.GOAL_NOT_FOUND,
                                        "Goal definition not found with id: " + request.getGoalId()));
                        entity.setGoalDefinition(goalDefinition);
                    }
                    return entity;
                })
                .collect(Collectors.toList());
        entities = repository.saveAll(entities);
        log.info("Created {} curricula", entities.size());
        return mapper.toResponseList(entities);
    }

    @Override
    @Transactional
    public CurriculumResponse partialUpdate(UUID curriculumId, CurriculumUpdateRequest request) {
        log.info("Updating curriculum: {}", curriculumId);
        Curriculum entity = findByIdOrThrow(curriculumId);

        // Update goal definition if provided
        if (request.getGoalId() != null) {
            GoalDefinition goalDefinition = goalDefinitionRepository.findByIdAndDeletedFalse(request.getGoalId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.GOAL_NOT_FOUND,
                            "Goal definition not found with id: " + request.getGoalId()));
            entity.setGoalDefinition(goalDefinition);
        }

        mapper.partialUpdate(entity, request);
        entity = repository.save(entity);
        log.info("Updated curriculum: {}", curriculumId);
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void softDelete(UUID curriculumId) {
        log.info("Soft deleting curriculum: {}", curriculumId);
        if (!repository.existsById(curriculumId)) {
            throw new ResourceNotFoundException(ErrorCode.CURRICULUM_NOT_FOUND,
                    "Curriculum not found with id: " + curriculumId);
        }
        repository.softDeleteById(curriculumId, Instant.now());
        log.info("Soft deleted curriculum: {}", curriculumId);
    }

    private Curriculum findByIdOrThrow(UUID curriculumId) {
        return repository.findByIdAndDeletedFalse(curriculumId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.CURRICULUM_NOT_FOUND,
                        "Curriculum not found with id: " + curriculumId));
    }
}
