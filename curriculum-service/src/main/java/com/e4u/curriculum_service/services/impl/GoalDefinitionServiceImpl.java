package com.e4u.curriculum_service.services.impl;

import com.e4u.curriculum_service.common.exception.ErrorCode;
import com.e4u.curriculum_service.common.exception.ResourceNotFoundException;
import com.e4u.curriculum_service.entities.GoalDefinition;
import com.e4u.curriculum_service.mapper.GoalDefinitionMapper;
import com.e4u.curriculum_service.models.request.GoalDefinitionCreateRequest;
import com.e4u.curriculum_service.models.request.GoalDefinitionFilterRequest;
import com.e4u.curriculum_service.models.request.GoalDefinitionUpdateRequest;
import com.e4u.curriculum_service.models.response.GoalDefinitionResponse;
import com.e4u.curriculum_service.repositories.GoalDefinitionRepository;
import com.e4u.curriculum_service.repositories.UserGoalRepository;
import com.e4u.curriculum_service.services.GoalDefinitionService;
import com.e4u.curriculum_service.repositories.specification.GoalDefinitionSpecification;
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
 * Service implementation for GoalDefinition operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GoalDefinitionServiceImpl implements GoalDefinitionService {

    private final GoalDefinitionRepository repository;
    private final UserGoalRepository userGoalRepository;
    private final GoalDefinitionMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<GoalDefinitionResponse> getAll(int page, int size, String sortBy, String sortDirection) {
        log.debug("Fetching all goal definitions - page: {}, size: {}", page, size);
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return repository.findByDeletedFalse(pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public GoalDefinitionResponse getById(UUID goalId) {
        log.debug("Fetching goal definition by id: {}", goalId);
        GoalDefinition entity = findByIdOrThrow(goalId);
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoalDefinitionResponse> getByUser(UUID userId) {
        log.debug("Fetching goal definitions for user: {}", userId);
        // TODO: Validate user exists via user-service when implemented
        return userGoalRepository.findByUserIdWithGoalDetails(userId)
                .stream()
                .map(ug -> mapper.toResponse(ug.getGoalDefinition()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GoalDefinitionResponse> filter(GoalDefinitionFilterRequest filterRequest) {
        log.debug("Filtering goal definitions with request: {}", filterRequest);
        Sort sort = Sort.by(Sort.Direction.fromString(filterRequest.getSortDirection()), filterRequest.getSortBy());
        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize(), sort);
        return repository.findAll(GoalDefinitionSpecification.withFilter(filterRequest), pageable)
                .map(mapper::toResponse);
    }

    @Override
    @Transactional
    public GoalDefinitionResponse create(GoalDefinitionCreateRequest request) {
        log.info("Creating new goal definition: {}", request.getGoalName());
        GoalDefinition entity = mapper.toEntity(request);
        entity = repository.save(entity);
        log.info("Created goal definition with id: {}", entity.getId());
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public List<GoalDefinitionResponse> createBatch(List<GoalDefinitionCreateRequest> requests) {
        log.info("Creating {} goal definitions in batch", requests.size());
        List<GoalDefinition> entities = requests.stream()
                .map(mapper::toEntity)
                .collect(Collectors.toList());
        entities = repository.saveAll(entities);
        log.info("Created {} goal definitions", entities.size());
        return mapper.toResponseList(entities);
    }

    @Override
    @Transactional
    public GoalDefinitionResponse partialUpdate(UUID goalId, GoalDefinitionUpdateRequest request) {
        log.info("Updating goal definition: {}", goalId);
        GoalDefinition entity = findByIdOrThrow(goalId);
        mapper.partialUpdate(entity, request);
        entity = repository.save(entity);
        log.info("Updated goal definition: {}", goalId);
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void softDelete(UUID goalId) {
        log.info("Soft deleting goal definition: {}", goalId);
        if (!repository.existsById(goalId)) {
            throw new ResourceNotFoundException(ErrorCode.GOAL_NOT_FOUND,
                    "Goal definition not found with id: " + goalId);
        }
        repository.softDeleteById(goalId, Instant.now());
        log.info("Soft deleted goal definition: {}", goalId);
    }

    private GoalDefinition findByIdOrThrow(UUID goalId) {
        return repository.findByIdAndDeletedFalse(goalId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.GOAL_NOT_FOUND,
                        "Goal definition not found with id: " + goalId));
    }
}
