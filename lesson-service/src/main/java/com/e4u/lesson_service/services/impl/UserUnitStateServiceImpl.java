package com.e4u.lesson_service.services.impl;

import com.e4u.lesson_service.client.CurriculumServiceClient;
import com.e4u.lesson_service.client.dto.CurriculumServiceResponse;
import com.e4u.lesson_service.client.dto.CurriculumUnitDTO;
import com.e4u.lesson_service.common.exception.ErrorCode;
import com.e4u.lesson_service.common.exception.ResourceNotFoundException;
import com.e4u.lesson_service.common.exception.ServiceException;
import com.e4u.lesson_service.entities.UserUnitState;
import com.e4u.lesson_service.entities.UserUnitState.UnitStatus;
import com.e4u.lesson_service.models.request.UserUnitStateFilterRequest;
import com.e4u.lesson_service.models.response.UserUnitStateResponse;
import com.e4u.lesson_service.repositories.UserUnitStateRepository;
import com.e4u.lesson_service.services.UserUnitStateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of UserUnitStateService.
 * Combines data from curriculum-service (unit details) and local database (user
 * state).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserUnitStateServiceImpl implements UserUnitStateService {

    private final UserUnitStateRepository userUnitStateRepository;
    private final CurriculumServiceClient curriculumServiceClient;

    @Override
    @Transactional(readOnly = true)
    public List<UserUnitStateResponse> getUnitsByCurriculumWithState(UUID curriculumId, UUID userId) {
        log.debug("Fetching units for curriculum: {} with state for user: {}", curriculumId, userId);

        // 1. Fetch units from curriculum-service
        CurriculumServiceResponse<List<CurriculumUnitDTO>> curriculumResponse = curriculumServiceClient
                .getUnitsByCurriculumId(curriculumId);

        if (!curriculumResponse.isSuccess() || curriculumResponse.getData() == null) {
            log.warn("Failed to fetch units from curriculum-service for curriculum: {}", curriculumId);
            throw new ServiceException(ErrorCode.EXTERNAL_SERVICE_ERROR,
                    "Failed to fetch units from curriculum service: " + curriculumResponse.getMessage());
        }

        List<CurriculumUnitDTO> units = curriculumResponse.getData();
        if (units.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Fetch user states for this user
        List<UserUnitState> userStates = userUnitStateRepository.findByUserId(userId);
        Map<UUID, UserUnitState> statesByUnitId = userStates.stream()
                .collect(Collectors.toMap(UserUnitState::getUnitId, Function.identity(), (a, b) -> a));

        // 3. Combine unit info with user state
        return units.stream()
                .map(unit -> buildResponse(unit, statesByUnitId.get(unit.getId())))
                .sorted(Comparator.comparing(UserUnitStateResponse::getDefaultOrder,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserUnitStateResponse> getUnitsByCurriculumAndStatus(UUID curriculumId, UUID userId,
            UnitStatus status) {
        log.debug("Fetching units for curriculum: {} with status: {} for user: {}", curriculumId, status, userId);

        List<UserUnitStateResponse> allUnits = getUnitsByCurriculumWithState(curriculumId, userId);

        return allUnits.stream()
                .filter(unit -> {
                    if (status == UnitStatus.NOT_STARTED) {
                        // Units without state or with NOT_STARTED status
                        return unit.getStatus() == null || unit.getStatus() == UnitStatus.NOT_STARTED;
                    }
                    return status.equals(unit.getStatus());
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserUnitStateResponse> filter(UserUnitStateFilterRequest filterRequest) {
        log.debug("Filtering user unit states with criteria: {}", filterRequest);

        if (filterRequest.getUserId() == null && filterRequest.getCurriculumId() == null) {
            throw new IllegalArgumentException("Either userId or curriculumId must be provided");
        }

        List<UserUnitStateResponse> results;

        if (filterRequest.getCurriculumId() != null && filterRequest.getUserId() != null) {
            // Filter by curriculum and user
            results = getUnitsByCurriculumWithState(filterRequest.getCurriculumId(), filterRequest.getUserId());
        } else if (filterRequest.getUserId() != null) {
            // Filter by user only
            results = getAllByUserId(filterRequest.getUserId());
        } else {
            // Filter by curriculum only - return units without state info
            CurriculumServiceResponse<List<CurriculumUnitDTO>> curriculumResponse = curriculumServiceClient
                    .getUnitsByCurriculumId(filterRequest.getCurriculumId());

            if (!curriculumResponse.isSuccess() || curriculumResponse.getData() == null) {
                results = Collections.emptyList();
            } else {
                results = curriculumResponse.getData().stream()
                        .map(unit -> buildResponse(unit, null))
                        .collect(Collectors.toList());
            }
        }

        // Apply additional filters
        results = applyFilters(results, filterRequest);

        // Apply sorting
        results = applySorting(results, filterRequest.getSortBy(), filterRequest.getSortDirection());

        // Apply pagination
        return applyPagination(results, filterRequest.getPage(), filterRequest.getSize());
    }

    @Override
    @Transactional(readOnly = true)
    public UserUnitStateResponse getUnitWithState(UUID unitId, UUID userId) {
        log.debug("Fetching unit: {} with state for user: {}", unitId, userId);

        // 1. Fetch unit from curriculum-service
        CurriculumServiceResponse<CurriculumUnitDTO> curriculumResponse = curriculumServiceClient.getUnitById(unitId);

        if (!curriculumResponse.isSuccess() || curriculumResponse.getData() == null) {
            throw new ResourceNotFoundException(ErrorCode.UNIT_NOT_FOUND,
                    "Unit not found with id: " + unitId);
        }

        // 2. Fetch user state
        Optional<UserUnitState> userState = userUnitStateRepository.findByUserIdAndUnitId(userId, unitId);

        // 3. Combine and return
        return buildResponse(curriculumResponse.getData(), userState.orElse(null));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserUnitStateResponse> getAllByUserId(UUID userId) {
        log.debug("Fetching all unit states for user: {}", userId);

        List<UserUnitState> userStates = userUnitStateRepository.findByUserId(userId);

        if (userStates.isEmpty()) {
            return Collections.emptyList();
        }

        // Fetch unit details for each state
        return userStates.stream()
                .map(state -> {
                    try {
                        CurriculumServiceResponse<CurriculumUnitDTO> response = curriculumServiceClient
                                .getUnitById(state.getUnitId());
                        if (response.isSuccess() && response.getData() != null) {
                            return buildResponse(response.getData(), state);
                        }
                        return buildResponseFromStateOnly(state);
                    } catch (Exception e) {
                        log.warn("Failed to fetch unit details for unitId: {}", state.getUnitId(), e);
                        return buildResponseFromStateOnly(state);
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Build a combined response from unit DTO and user state.
     */
    private UserUnitStateResponse buildResponse(CurriculumUnitDTO unit, UserUnitState state) {
        UserUnitStateResponse.UserUnitStateResponseBuilder builder = UserUnitStateResponse.builder()
                // Unit information
                .unitId(unit.getId())
                .curriculumId(unit.getCurriculumId())
                .curriculumName(unit.getCurriculumName())
                .unitName(unit.getUnitName())
                .requiredProficiencyLevel(unit.getRequiredProficiencyLevel())
                .defaultOrder(unit.getDefaultOrder())
                .baseKeywords(unit.getBaseKeywords())
                .description(unit.getDescription())
                .isActive(unit.getIsActive())
                .wordCount(unit.getWordCount());

        // Add user state if available
        if (state != null) {
            builder.stateId(state.getId())
                    .userId(state.getUserId())
                    .status(state.getStatus())
                    .currentPriorityScore(state.getCurrentPriorityScore())
                    .isFastTracked(state.getIsFastTracked())
                    .proficiencyScore(state.getProficiencyScore())
                    .difficultyModifier(state.getDifficultyModifier())
                    .lessonCount(state.getLessons() != null ? state.getLessons().size() : 0)
                    .lastInteractionAt(state.getLastInteractionAt())
                    .stateCreatedAt(state.getCreatedAt())
                    .stateUpdatedAt(state.getUpdatedAt());
        } else {
            // Default status for units without state
            builder.status(UnitStatus.NOT_STARTED);
        }

        return builder.build();
    }

    /**
     * Build a response from state only when unit details are unavailable.
     */
    private UserUnitStateResponse buildResponseFromStateOnly(UserUnitState state) {
        return UserUnitStateResponse.builder()
                .unitId(state.getUnitId())
                .stateId(state.getId())
                .userId(state.getUserId())
                .status(state.getStatus())
                .currentPriorityScore(state.getCurrentPriorityScore())
                .isFastTracked(state.getIsFastTracked())
                .proficiencyScore(state.getProficiencyScore())
                .difficultyModifier(state.getDifficultyModifier())
                .lessonCount(state.getLessons() != null ? state.getLessons().size() : 0)
                .lastInteractionAt(state.getLastInteractionAt())
                .stateCreatedAt(state.getCreatedAt())
                .stateUpdatedAt(state.getUpdatedAt())
                .build();
    }

    /**
     * Apply filters to the result list.
     */
    private List<UserUnitStateResponse> applyFilters(List<UserUnitStateResponse> results,
            UserUnitStateFilterRequest filter) {
        return results.stream()
                .filter(r -> filter.getStatus() == null || filter.getStatus().equals(r.getStatus()))
                .filter(r -> filter.getIsFastTracked() == null
                        || filter.getIsFastTracked().equals(r.getIsFastTracked()))
                .filter(r -> filter.getMinProficiencyScore() == null ||
                        (r.getProficiencyScore() != null && r.getProficiencyScore() >= filter.getMinProficiencyScore()))
                .filter(r -> filter.getMaxProficiencyScore() == null ||
                        (r.getProficiencyScore() != null && r.getProficiencyScore() <= filter.getMaxProficiencyScore()))
                .filter(r -> filter.getMinPriorityScore() == null ||
                        (r.getCurrentPriorityScore() != null
                                && r.getCurrentPriorityScore() >= filter.getMinPriorityScore()))
                .filter(r -> filter.getMaxPriorityScore() == null ||
                        (r.getCurrentPriorityScore() != null
                                && r.getCurrentPriorityScore() <= filter.getMaxPriorityScore()))
                .collect(Collectors.toList());
    }

    /**
     * Apply sorting to the result list.
     */
    private List<UserUnitStateResponse> applySorting(List<UserUnitStateResponse> results,
            String sortBy, String sortDirection) {
        Comparator<UserUnitStateResponse> comparator = getComparator(sortBy);

        if ("DESC".equalsIgnoreCase(sortDirection)) {
            comparator = comparator.reversed();
        }

        return results.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    /**
     * Get comparator for the specified sort field.
     */
    private Comparator<UserUnitStateResponse> getComparator(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "unitname" -> Comparator.comparing(UserUnitStateResponse::getUnitName,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case "status" -> Comparator.comparing(r -> r.getStatus() != null ? r.getStatus().name() : "",
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case "proficiencyscore" -> Comparator.comparing(UserUnitStateResponse::getProficiencyScore,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case "currentpriorityscore" -> Comparator.comparing(UserUnitStateResponse::getCurrentPriorityScore,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case "lastinteractionat" -> Comparator.comparing(UserUnitStateResponse::getLastInteractionAt,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            case "statecreatedat" -> Comparator.comparing(UserUnitStateResponse::getStateCreatedAt,
                    Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparing(UserUnitStateResponse::getDefaultOrder,
                    Comparator.nullsLast(Comparator.naturalOrder()));
        };
    }

    /**
     * Apply pagination to the result list.
     */
    private Page<UserUnitStateResponse> applyPagination(List<UserUnitStateResponse> results,
            int page, int size) {
        int totalElements = results.size();
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalElements);

        if (fromIndex >= totalElements) {
            return new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), totalElements);
        }

        List<UserUnitStateResponse> pagedResults = results.subList(fromIndex, toIndex);
        return new PageImpl<>(pagedResults, PageRequest.of(page, size), totalElements);
    }
}
