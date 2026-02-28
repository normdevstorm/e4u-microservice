package com.e4u.learning_service.controllers;

import com.e4u.learning_service.common.utils.SecurityUtil;
import com.e4u.learning_service.dtos.request.GoalDefinitionCreateRequest;
import com.e4u.learning_service.dtos.request.GoalDefinitionFilterRequest;
import com.e4u.learning_service.dtos.request.GoalDefinitionUpdateRequest;
import com.e4u.learning_service.dtos.response.BaseResponse;
import com.e4u.learning_service.dtos.response.GoalDefinitionResponse;
import com.e4u.learning_service.dtos.response.PagedResponse;
import com.e4u.learning_service.services.GoalDefinitionService;
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
 * REST Controller for managing Goal Definitions.
 */
@RestController
@RequestMapping("/v1/goals")
@RequiredArgsConstructor
@Tag(name = "Goal Definitions", description = "APIs for managing learning goal definitions")
public class GoalDefinitionController {

        private final GoalDefinitionService service;

        @GetMapping
        @Operation(summary = "Get all goal definitions", description = "Retrieve all goal definitions with pagination")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved goal definitions")
        })
        public ResponseEntity<PagedResponse<GoalDefinitionResponse>> getAll(
                        @Parameter(name = "page", description = "Page number (0-indexed)", example = "0") @RequestParam(name = "page", defaultValue = "0") int page,
                        @Parameter(name = "size", description = "Page size", example = "20") @RequestParam(name = "size", defaultValue = "20") int size,
                        @Parameter(name = "sortBy", description = "Sort field", example = "createdAt") @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                        @Parameter(name = "sortDirection", description = "Sort direction (ASC/DESC)", example = "DESC") @RequestParam(name = "sortDirection", defaultValue = "DESC") String sortDirection) {
                Page<GoalDefinitionResponse> result = service.getAll(page, size, sortBy, sortDirection);
                return ResponseEntity.ok(PagedResponse.of(result));
        }

        @GetMapping("/{goalId}")
        @Operation(summary = "Get goal definition by ID", description = "Retrieve a specific goal definition by its ID")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved the goal definition"),
                        @ApiResponse(responseCode = "404", description = "Goal definition not found")
        })
        public ResponseEntity<BaseResponse<GoalDefinitionResponse>> getById(
                        @Parameter(description = "Goal ID") @PathVariable("goalId") UUID goalId) {
                GoalDefinitionResponse result = service.getById(goalId);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/user")
        @Operation(summary = "Get goals by user", description = "Retrieve all goals associated with a specific user")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved user's goals")
        })
        public ResponseEntity<BaseResponse<List<GoalDefinitionResponse>>> getByUser() {
                UUID userId = SecurityUtil.getCurrentUserId();
                List<GoalDefinitionResponse> result = service.getByUser(userId);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @PostMapping("/filter")
        @Operation(summary = "Filter goal definitions", description = "Filter goal definitions with various criteria")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully filtered goal definitions")
        })
        public ResponseEntity<PagedResponse<GoalDefinitionResponse>> filter(
                        @RequestBody GoalDefinitionFilterRequest filterRequest) {
                Page<GoalDefinitionResponse> result = service.filter(filterRequest);
                return ResponseEntity.ok(PagedResponse.of(result));
        }

        @PostMapping
        @Operation(summary = "Create a goal definition", description = "Create a new goal definition")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Goal definition created successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid request data")
        })
        public ResponseEntity<BaseResponse<GoalDefinitionResponse>> create(
                        @Valid @RequestBody GoalDefinitionCreateRequest request) {
                GoalDefinitionResponse result = service.create(request);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(BaseResponse.ok(result, "Goal definition created successfully"));
        }

        @PostMapping("/batch")
        @Operation(summary = "Create multiple goal definitions", description = "Create multiple goal definitions in batch")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Goal definitions created successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid request data")
        })
        public ResponseEntity<BaseResponse<List<GoalDefinitionResponse>>> createBatch(
                        @Valid @RequestBody List<GoalDefinitionCreateRequest> requests) {
                List<GoalDefinitionResponse> result = service.createBatch(requests);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(BaseResponse.ok(result, "Goal definitions created successfully"));
        }

        @PatchMapping("/{goalId}")
        @Operation(summary = "Update a goal definition", description = "Partially update an existing goal definition")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Goal definition updated successfully"),
                        @ApiResponse(responseCode = "404", description = "Goal definition not found"),
                        @ApiResponse(responseCode = "400", description = "Invalid request data")
        })
        public ResponseEntity<BaseResponse<GoalDefinitionResponse>> partialUpdate(
                        @Parameter(description = "Goal ID") @PathVariable("goalId") UUID goalId,
                        @RequestBody GoalDefinitionUpdateRequest request) {
                GoalDefinitionResponse result = service.partialUpdate(goalId, request);
                return ResponseEntity.ok(BaseResponse.ok(result, "Goal definition updated successfully"));
        }

        @DeleteMapping("/{goalId}")
        @Operation(summary = "Delete a goal definition", description = "Soft delete a goal definition")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Goal definition deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "Goal definition not found")
        })
        public ResponseEntity<BaseResponse<Void>> softDelete(
                        @Parameter(description = "Goal ID") @PathVariable("goalId") UUID goalId) {
                service.softDelete(goalId);
                return ResponseEntity.ok(BaseResponse.ok("Goal definition deleted successfully"));
        }
}
