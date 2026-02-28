package com.e4u.learning_service.services;

import com.e4u.learning_service.dtos.request.UserUnitStateFilterRequest;
import com.e4u.learning_service.dtos.response.UserUnitStateResponse;
import com.e4u.learning_service.entities.UserUnitState.UnitStatus;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for UserUnitState operations.
 * Provides combined data from lesson-service (user state) and
 * curriculum-service (unit details).
 */
public interface UserUnitStateService {

    /**
     * Get all units for a curriculum with user's learning state.
     * Combines unit information from curriculum-service with state from
     * lesson-service.
     * 
     * @param curriculumId The curriculum ID
     * @param userId       The user ID
     * @return List of units with user's state
     */
    List<UserUnitStateResponse> getUnitsByCurriculumWithState(UUID curriculumId, UUID userId);

    /**
     * Get units filtered by status for a curriculum.
     * 
     * @param curriculumId The curriculum ID
     * @param userId       The user ID
     * @param status       The status to filter by (NOT_STARTED, IN_PROGRESS,
     *                     COMPLETED)
     * @return List of filtered units with user's state
     */
    List<UserUnitStateResponse> getUnitsByCurriculumAndStatus(UUID curriculumId, UUID userId, UnitStatus status);

    /**
     * Filter user unit states with various criteria.
     * 
     * @param filterRequest The filter criteria
     * @return Paginated list of units with user's state
     */
    Page<UserUnitStateResponse> filter(UserUnitStateFilterRequest filterRequest);

    /**
     * Get a specific unit with user's state.
     * 
     * @param unitId The unit ID
     * @param userId The user ID
     * @return Unit with user's state
     */
    UserUnitStateResponse getUnitWithState(UUID unitId, UUID userId);

    /**
     * Get all unit states for a user.
     * 
     * @param userId The user ID
     * @return List of all units the user has interacted with
     */
    List<UserUnitStateResponse> getAllByUserId(UUID userId);
}
