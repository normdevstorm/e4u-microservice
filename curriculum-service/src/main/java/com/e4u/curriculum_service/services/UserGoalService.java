package com.e4u.curriculum_service.services;

import com.e4u.curriculum_service.models.request.UserGoalCreateRequest;
import com.e4u.curriculum_service.models.request.UserGoalUpdateRequest;
import com.e4u.curriculum_service.models.response.UserGoalResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for UserGoal operations.
 */
public interface UserGoalService {

    Page<UserGoalResponse> getAll(int page, int size, String sortBy, String sortDirection);

    UserGoalResponse getById(UUID userId, UUID goalId);

    List<UserGoalResponse> getByUser(UUID userId);

    List<UserGoalResponse> getByGoal(UUID goalId);

    UserGoalResponse addGoalForUser(UserGoalCreateRequest request);

    List<UserGoalResponse> addGoalsForUser(UUID userId, List<UUID> goalIds);

    UserGoalResponse partialUpdate(UUID userId, UUID goalId, UserGoalUpdateRequest request);

    void softDelete(UUID userId, UUID goalId);

    void softDeleteAllForUser(UUID userId);
}
