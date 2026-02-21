package com.e4u.curriculum_service.services.impl;

import com.e4u.curriculum_service.common.exception.AppException;
import com.e4u.curriculum_service.common.exception.ErrorCode;
import com.e4u.curriculum_service.common.exception.ResourceNotFoundException;
import com.e4u.curriculum_service.entities.GoalDefinition;
import com.e4u.curriculum_service.entities.UserGoal;
import com.e4u.curriculum_service.mapper.UserGoalMapper;
import com.e4u.curriculum_service.models.request.UserGoalCreateRequest;
import com.e4u.curriculum_service.models.request.UserGoalUpdateRequest;
import com.e4u.curriculum_service.models.response.UserGoalResponse;
import com.e4u.curriculum_service.repositories.GoalDefinitionRepository;
import com.e4u.curriculum_service.repositories.UserGoalRepository;
import com.e4u.curriculum_service.services.UserGoalService;
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
import java.util.stream.Collectors;

/**
 * Service implementation for UserGoal operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserGoalServiceImpl implements UserGoalService {

    private final UserGoalRepository repository;
    private final GoalDefinitionRepository goalDefinitionRepository;
    private final UserGoalMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Page<UserGoalResponse> getAll(int page, int size, String sortBy, String sortDirection) {
        log.debug("Fetching all user goals - page: {}, size: {}", page, size);
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return repository.findByDeletedFalse(pageable).map(mapper::toSimpleResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UserGoalResponse getById(UUID userId, UUID goalId) {
        log.debug("Fetching user goal - userId: {}, goalId: {}", userId, goalId);
        UserGoal entity = findByIdOrThrow(userId, goalId);
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserGoalResponse> getByUser(UUID userId) {
        log.debug("Fetching goals for user: {}", userId);
        // TODO: Validate user exists via user-service when implemented
        return repository.findByUserIdWithGoalDetails(userId)
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserGoalResponse> getByGoal(UUID goalId) {
        log.debug("Fetching users for goal: {}", goalId);
        return repository.findByGoalDefinitionIdAndDeletedFalse(goalId)
                .stream()
                .map(mapper::toSimpleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserGoalResponse addGoalForUser(UserGoalCreateRequest request) {
        log.info("Adding goal {} for user {}", request.getGoalId(), request.getUserId());

        // TODO: Validate user exists via user-service when implemented

        // Check if user already has this goal
        if (repository.existsByUserIdAndGoalDefinitionIdAndDeletedFalse(request.getUserId(), request.getGoalId())) {
            throw new AppException(ErrorCode.USER_GOAL_ALREADY_EXISTS,
                    "User already has goal with id: " + request.getGoalId());
        }

        // Get goal definition
        GoalDefinition goalDefinition = goalDefinitionRepository.findByIdAndDeletedFalse(request.getGoalId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.GOAL_NOT_FOUND,
                        "Goal definition not found with id: " + request.getGoalId()));

        // If this goal is marked as primary, clear other primary goals for this user
        if (Boolean.TRUE.equals(request.getIsPrimary())) {
            repository.clearPrimaryForUser(request.getUserId());
        }

        UserGoal entity = mapper.toEntity(request);
        entity.setGoalDefinition(goalDefinition);
        entity.setStartedAt(Instant.now());
        entity = repository.save(entity);

        log.info("Added goal {} for user {}", request.getGoalId(), request.getUserId());
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public List<UserGoalResponse> addGoalsForUser(UUID userId, List<UUID> goalIds) {
        log.info("Adding {} goals for user {}", goalIds.size(), userId);

        // TODO: Validate user exists via user-service when implemented

        List<UserGoalResponse> responses = new ArrayList<>();
        for (UUID goalId : goalIds) {
            if (!repository.existsByUserIdAndGoalDefinitionIdAndDeletedFalse(userId, goalId)) {
                GoalDefinition goalDefinition = goalDefinitionRepository.findByIdAndDeletedFalse(goalId)
                        .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.GOAL_NOT_FOUND,
                                "Goal definition not found with id: " + goalId));

                UserGoal entity = UserGoal.builder()
                        .userId(userId)
                        .goalId(goalDefinition.getId())
                        .goalDefinition(goalDefinition)
                        .isPrimary(false)
                        .startedAt(Instant.now())
                        .build();
                entity = repository.save(entity);
                responses.add(mapper.toResponse(entity));
            }
        }

        log.info("Added {} goals for user {}", responses.size(), userId);
        return responses;
    }

    @Override
    @Transactional
    public UserGoalResponse partialUpdate(UUID userId, UUID goalId, UserGoalUpdateRequest request) {
        log.info("Updating user goal - userId: {}, goalId: {}", userId, goalId);
        UserGoal entity = findByIdOrThrow(userId, goalId);

        // If setting this goal as primary, clear other primary goals
        if (Boolean.TRUE.equals(request.getIsPrimary())) {
            repository.clearPrimaryForUser(userId);
        }

        mapper.partialUpdate(entity, request);
        entity = repository.save(entity);
        log.info("Updated user goal - userId: {}, goalId: {}", userId, goalId);
        return mapper.toResponse(entity);
    }

    @Override
    @Transactional
    public void softDelete(UUID userId, UUID goalId) {
        log.info("Soft deleting user goal - userId: {}, goalId: {}", userId, goalId);
        if (!repository.existsByUserIdAndGoalDefinitionIdAndDeletedFalse(userId, goalId)) {
            throw new ResourceNotFoundException(ErrorCode.USER_GOAL_NOT_FOUND,
                    "User goal not found - userId: " + userId + ", goalId: " + goalId);
        }
        repository.softDeleteByUserIdAndGoalId(userId, goalId, Instant.now());
        log.info("Soft deleted user goal - userId: {}, goalId: {}", userId, goalId);
    }

    @Override
    @Transactional
    public void softDeleteAllForUser(UUID userId) {
        log.info("Soft deleting all goals for user: {}", userId);
        // TODO: Validate user exists via user-service when implemented
        repository.softDeleteAllByUserId(userId, Instant.now());
        log.info("Soft deleted all goals for user: {}", userId);
    }

    private UserGoal findByIdOrThrow(UUID userId, UUID goalId) {
        return repository.findByUserIdAndGoalDefinitionIdAndDeletedFalse(userId, goalId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_GOAL_NOT_FOUND,
                        "User goal not found - userId: " + userId + ", goalId: " + goalId));
    }
}
