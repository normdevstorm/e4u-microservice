package com.e4u.curriculum_service.services;

import com.e4u.curriculum_service.models.request.GoalDefinitionCreateRequest;
import com.e4u.curriculum_service.models.request.GoalDefinitionFilterRequest;
import com.e4u.curriculum_service.models.request.GoalDefinitionUpdateRequest;
import com.e4u.curriculum_service.models.response.GoalDefinitionResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for GoalDefinition operations.
 */
public interface GoalDefinitionService {

    Page<GoalDefinitionResponse> getAll(int page, int size, String sortBy, String sortDirection);

    GoalDefinitionResponse getById(UUID goalId);

    List<GoalDefinitionResponse> getByUser(UUID userId);

    Page<GoalDefinitionResponse> filter(GoalDefinitionFilterRequest filterRequest);

    GoalDefinitionResponse create(GoalDefinitionCreateRequest request);

    List<GoalDefinitionResponse> createBatch(List<GoalDefinitionCreateRequest> requests);

    GoalDefinitionResponse partialUpdate(UUID goalId, GoalDefinitionUpdateRequest request);

    void softDelete(UUID goalId);
}
