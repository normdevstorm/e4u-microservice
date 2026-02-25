package com.e4u.lesson_service.controllers;

import com.e4u.lesson_service.entities.UserUnitState.UnitStatus;
import com.e4u.lesson_service.models.request.UserUnitStateFilterRequest;
import com.e4u.lesson_service.models.response.BaseResponse;
import com.e4u.lesson_service.models.response.PagedResponse;
import com.e4u.lesson_service.models.response.UserUnitStateResponse;
import com.e4u.lesson_service.services.UserUnitStateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for UserUnitState operations.
 * Provides APIs for managing user's learning progress on curriculum units.
 * Combines data from curriculum-service (unit details) and lesson-service (user
 * state).
 */
@RestController
@RequestMapping("/v1/user-unit-states")
@RequiredArgsConstructor
@Tag(name = "User Unit States", description = "APIs for managing user's learning progress on curriculum units")
public class UserUnitStateController {

        private final UserUnitStateService service;

        // ==================== GET Operations ====================

        @GetMapping("/curriculum/{curriculumId}/user/{userId}")
        @Operation(summary = "Get all units by curriculum with user state", description = "Retrieve all units for a specific curriculum combined with the user's learning state. "
                        +
                        "Returns unit information from curriculum-service merged with user progress from lesson-service.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved units with state"),
                        @ApiResponse(responseCode = "404", description = "Curriculum not found"),
                        @ApiResponse(responseCode = "503", description = "Curriculum service unavailable")
        })
        public ResponseEntity<BaseResponse<List<UserUnitStateResponse>>> getUnitsByCurriculumWithState(
                        @Parameter(description = "Curriculum ID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("curriculumId") UUID curriculumId,
                        @Parameter(description = "User ID", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11") @PathVariable("userId") UUID userId) {

                List<UserUnitStateResponse> result = service.getUnitsByCurriculumWithState(curriculumId, userId);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/curriculum/{curriculumId}/user/{userId}/status/{status}")
        @Operation(summary = "Get units by curriculum filtered by status", description = "Retrieve units for a curriculum filtered by user's learning status. "
                        +
                        "Supported statuses: NOT_STARTED, IN_PROGRESS, COMPLETED.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved filtered units"),
                        @ApiResponse(responseCode = "400", description = "Invalid status value"),
                        @ApiResponse(responseCode = "404", description = "Curriculum not found")
        })
        public ResponseEntity<BaseResponse<List<UserUnitStateResponse>>> getUnitsByCurriculumAndStatus(
                        @Parameter(description = "Curriculum ID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("curriculumId") UUID curriculumId,
                        @Parameter(description = "User ID", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11") @PathVariable("userId") UUID userId,
                        @Parameter(description = "Unit status to filter by", example = "IN_PROGRESS") @PathVariable("status") UnitStatus status) {

                List<UserUnitStateResponse> result = service.getUnitsByCurriculumAndStatus(curriculumId, userId,
                                status);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/unit/{unitId}/user/{userId}")
        @Operation(summary = "Get unit with user state", description = "Retrieve a specific unit's details combined with the user's learning state.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved unit with state"),
                        @ApiResponse(responseCode = "404", description = "Unit not found")
        })
        public ResponseEntity<BaseResponse<UserUnitStateResponse>> getUnitWithState(
                        @Parameter(description = "Unit ID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("unitId") UUID unitId,
                        @Parameter(description = "User ID", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11") @PathVariable("userId") UUID userId) {

                UserUnitStateResponse result = service.getUnitWithState(unitId, userId);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/user/{userId}")
        @Operation(summary = "Get all unit states for a user", description = "Retrieve all units that a user has interacted with, including their learning progress.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved user's unit states")
        })
        public ResponseEntity<BaseResponse<List<UserUnitStateResponse>>> getAllByUserId(
                        @Parameter(description = "User ID", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11") @PathVariable("userId") UUID userId) {

                List<UserUnitStateResponse> result = service.getAllByUserId(userId);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/user/{userId}/status/{status}")
        @Operation(summary = "Get user's units filtered by status", description = "Retrieve all units for a user filtered by their learning status.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved filtered units")
        })
        public ResponseEntity<BaseResponse<List<UserUnitStateResponse>>> getUserUnitsByStatus(
                        @Parameter(description = "User ID", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11") @PathVariable("userId") UUID userId,
                        @Parameter(description = "Unit status to filter by", example = "COMPLETED") @PathVariable("status") UnitStatus status) {

                UserUnitStateFilterRequest filterRequest = UserUnitStateFilterRequest.builder()
                                .userId(userId)
                                .status(status)
                                .build();

                Page<UserUnitStateResponse> result = service.filter(filterRequest);
                return ResponseEntity.ok(BaseResponse.ok(result.getContent()));
        }

        // ==================== FILTER Operations ====================

        @PostMapping("/filter")
        @Operation(summary = "Filter user unit states", description = "Filter user unit states with various criteria including status, proficiency score, "
                        +
                        "priority score, and fast-tracked flag. Supports pagination and sorting.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully filtered unit states"),
                        @ApiResponse(responseCode = "400", description = "Invalid filter criteria")
        })
        public ResponseEntity<PagedResponse<UserUnitStateResponse>> filter(
                        @RequestBody UserUnitStateFilterRequest filterRequest) {

                Page<UserUnitStateResponse> result = service.filter(filterRequest);
                return ResponseEntity.ok(PagedResponse.of(result));
        }

        @GetMapping("/curriculum/{curriculumId}/user/{userId}/filter")
        @Operation(summary = "Filter units by curriculum with query parameters", description = "Filter units for a curriculum using query parameters for status and pagination.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully filtered units")
        })
        public ResponseEntity<PagedResponse<UserUnitStateResponse>> filterByCurriculum(
                        @Parameter(description = "Curriculum ID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("curriculumId") UUID curriculumId,
                        @Parameter(description = "User ID", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11") @PathVariable("userId") UUID userId,
                        @Parameter(description = "Filter by status (NOT_STARTED, IN_PROGRESS, COMPLETED)") @RequestParam(name = "status", required = false) UnitStatus status,
                        @Parameter(description = "Filter by fast-tracked flag") @RequestParam(name = "isFastTracked", required = false) Boolean isFastTracked,
                        @Parameter(description = "Minimum proficiency score") @RequestParam(name = "minProficiencyScore", required = false) Float minProficiencyScore,
                        @Parameter(description = "Maximum proficiency score") @RequestParam(name = "maxProficiencyScore", required = false) Float maxProficiencyScore,
                        @Parameter(description = "Page number (0-indexed)", example = "0") @RequestParam(name = "page", defaultValue = "0") int page,
                        @Parameter(description = "Page size", example = "20") @RequestParam(name = "size", defaultValue = "20") int size,
                        @Parameter(description = "Sort field", example = "defaultOrder") @RequestParam(name = "sortBy", defaultValue = "defaultOrder") String sortBy,
                        @Parameter(description = "Sort direction (ASC/DESC)", example = "ASC") @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection) {

                UserUnitStateFilterRequest filterRequest = UserUnitStateFilterRequest.builder()
                                .curriculumId(curriculumId)
                                .userId(userId)
                                .status(status)
                                .isFastTracked(isFastTracked)
                                .minProficiencyScore(minProficiencyScore)
                                .maxProficiencyScore(maxProficiencyScore)
                                .page(page)
                                .size(size)
                                .sortBy(sortBy)
                                .sortDirection(sortDirection)
                                .build();

                Page<UserUnitStateResponse> result = service.filter(filterRequest);
                return ResponseEntity.ok(PagedResponse.of(result));
        }
}
