package com.e4u.learning_service.controllers;

import com.e4u.learning_service.common.utils.SecurityUtil;
import com.e4u.learning_service.dtos.request.UserGoalCreateRequest;
import com.e4u.learning_service.dtos.request.UserGoalUpdateRequest;
import com.e4u.learning_service.dtos.response.BaseResponse;
import com.e4u.learning_service.dtos.response.PagedResponse;
import com.e4u.learning_service.dtos.response.UserGoalResponse;
import com.e4u.learning_service.services.UserGoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for managing User Goals.
 */
@RestController
@RequestMapping("/v1/user-goals")
@RequiredArgsConstructor
@Tag(name = "User Goals", description = "APIs for managing user-goal associations")
public class UserGoalController {

        private final UserGoalService service;

        @GetMapping
        @Operation(summary = "Get all user goals", description = "Retrieve all user-goal associations with pagination")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved user goals")
        })
        public ResponseEntity<PagedResponse<UserGoalResponse>> getAll(
                        @Parameter(name = "page", description = "Page number (0-indexed)", example = "0") @RequestParam(name = "page", defaultValue = "0") int page,
                        @Parameter(name = "size", description = "Page size", example = "20") @RequestParam(name = "size", defaultValue = "20") int size,
                        @Parameter(name = "sortBy", description = "Sort field", example = "startedAt") @RequestParam(name = "sortBy", defaultValue = "startedAt") String sortBy,
                        @Parameter(name = "sortDirection", description = "Sort direction (ASC/DESC)", example = "DESC") @RequestParam(name = "sortDirection", defaultValue = "DESC") String sortDirection) {
                Page<UserGoalResponse> result = service.getAll(page, size, sortBy, sortDirection);
                return ResponseEntity.ok(PagedResponse.of(result));
        }

        @GetMapping("/goal/{goalId}")
        @Operation(summary = "Get specific user goal", description = "Retrieve a specific user-goal association")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved user goal"),
                        @ApiResponse(responseCode = "404", description = "User goal not found")
        })
        public ResponseEntity<BaseResponse<UserGoalResponse>> getById(
                        @Parameter(description = "Goal ID") @PathVariable("goalId") UUID goalId) {
                UUID userId = SecurityUtil.getCurrentUserId();
                UserGoalResponse result = service.getById(userId, goalId);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/user")
        @Operation(summary = "Get goals by user", description = "Retrieve all goals for a specific user")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved user's goals")
        })
        public ResponseEntity<BaseResponse<List<UserGoalResponse>>> getByUser() {
                UUID userId = SecurityUtil.getCurrentUserId();
                List<UserGoalResponse> result = service.getByUser(userId);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/goal/{goalId}/users")
        @Operation(summary = "Get users by goal", description = "Retrieve all users with a specific goal")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved users for goal")
        })
        public ResponseEntity<BaseResponse<List<UserGoalResponse>>> getByGoal(
                        @Parameter(description = "Goal ID") @PathVariable("goalId") UUID goalId) {
                List<UserGoalResponse> result = service.getByGoal(goalId);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @PostMapping
        @Operation(summary = "Add goal for user", description = "Associate a goal with a user")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "User goal created successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid request data"),
                        @ApiResponse(responseCode = "409", description = "User already has this goal")
        })
        public ResponseEntity<BaseResponse<UserGoalResponse>> addGoalForUser(
                        @Valid @RequestBody UserGoalCreateRequest request) {
                UserGoalResponse result = service.addGoalForUser(request);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(BaseResponse.ok(result, "Goal added for user successfully"));
        }

        @PostMapping("/user/batch")
        @Operation(summary = "Add multiple goals for user", description = "Associate multiple goals with a user")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "User goals created successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid request data")
        })
        public ResponseEntity<BaseResponse<List<UserGoalResponse>>> addGoalsForUser(
                        @RequestBody List<UUID> goalIds) {
                UUID userId = SecurityUtil.getCurrentUserId();
                List<UserGoalResponse> result = service.addGoalsForUser(userId, goalIds);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(BaseResponse.ok(result, "Goals added for user successfully"));
        }

        @PatchMapping("/goal/{goalId}")
        @Operation(summary = "Update user goal", description = "Partially update a user-goal association")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "User goal updated successfully"),
                        @ApiResponse(responseCode = "404", description = "User goal not found"),
                        @ApiResponse(responseCode = "400", description = "Invalid request data")
        })
        public ResponseEntity<BaseResponse<UserGoalResponse>> partialUpdate(
                        @Parameter(description = "Goal ID") @PathVariable("goalId") UUID goalId,
                        @RequestBody UserGoalUpdateRequest request) {
                UUID userId = SecurityUtil.getCurrentUserId();
                UserGoalResponse result = service.partialUpdate(userId, goalId, request);
                return ResponseEntity.ok(BaseResponse.ok(result, "User goal updated successfully"));
        }

        @DeleteMapping("/goal/{goalId}")
        @Operation(summary = "Delete user goal", description = "Soft delete a user-goal association")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "User goal deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "User goal not found")
        })
        public ResponseEntity<BaseResponse<Void>> softDelete(
                        @Parameter(description = "Goal ID") @PathVariable("goalId") UUID goalId) {
                UUID userId = SecurityUtil.getCurrentUserId();
                service.softDelete(userId, goalId);
                return ResponseEntity.ok(BaseResponse.ok("User goal deleted successfully"));
        }

        @DeleteMapping("/user")
        @Operation(summary = "Delete all user goals", description = "Soft delete all goals for a user")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "All user goals deleted successfully")
        })
        public ResponseEntity<BaseResponse<Void>> softDeleteAllForUser() {
                UUID userId = SecurityUtil.getCurrentUserId();
                service.softDeleteAllForUser(userId);
                return ResponseEntity.ok(BaseResponse.ok("All goals deleted for user successfully"));
        }
}
