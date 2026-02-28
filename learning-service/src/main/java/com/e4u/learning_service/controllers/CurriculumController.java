package com.e4u.learning_service.controllers;

import com.e4u.learning_service.dtos.request.CurriculumCreateRequest;
import com.e4u.learning_service.dtos.request.CurriculumFilterRequest;
import com.e4u.learning_service.dtos.request.CurriculumUpdateRequest;
import com.e4u.learning_service.dtos.response.BaseResponse;
import com.e4u.learning_service.dtos.response.CurriculumDetailResponse;
import com.e4u.learning_service.dtos.response.CurriculumResponse;
import com.e4u.learning_service.dtos.response.PagedResponse;
import com.e4u.learning_service.services.CurriculumService;
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
 * REST Controller for managing Curricula.
 */
@RestController
@RequestMapping("/v1/curricula")
@RequiredArgsConstructor
@Tag(name = "Curricula", description = "APIs for managing learning curricula")
public class CurriculumController {

        private final CurriculumService service;

        @GetMapping
        @Operation(summary = "Get all curricula", description = "Retrieve all curricula with pagination")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved curricula")
        })
        public ResponseEntity<PagedResponse<CurriculumResponse>> getAll(
                        @Parameter(name = "page", description = "Page number (0-indexed)", example = "0") @RequestParam(name = "page", defaultValue = "0") int page,
                        @Parameter(name = "size", description = "Page size", example = "20") @RequestParam(name = "size", defaultValue = "20") int size,
                        @Parameter(name = "sortBy", description = "Sort field", example = "createdAt") @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                        @Parameter(name = "sortDirection", description = "Sort direction (ASC/DESC)", example = "DESC") @RequestParam(name = "sortDirection", defaultValue = "DESC") String sortDirection) {
                Page<CurriculumResponse> result = service.getAll(page, size, sortBy, sortDirection);
                return ResponseEntity.ok(PagedResponse.of(result));
        }

        @GetMapping("/{curriculumId}")
        @Operation(summary = "Get curriculum by ID", description = "Retrieve a specific curriculum by its ID")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved the curriculum"),
                        @ApiResponse(responseCode = "404", description = "Curriculum not found")
        })
        public ResponseEntity<BaseResponse<CurriculumResponse>> getById(
                        @Parameter(description = "Curriculum ID") @PathVariable("curriculumId") UUID curriculumId) {
                CurriculumResponse result = service.getById(curriculumId);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/{curriculumId}/details")
        @Operation(summary = "Get curriculum with details", description = "Retrieve a curriculum with its units")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved curriculum with details"),
                        @ApiResponse(responseCode = "404", description = "Curriculum not found")
        })
        public ResponseEntity<BaseResponse<CurriculumDetailResponse>> getByIdWithDetails(
                        @Parameter(description = "Curriculum ID") @PathVariable("curriculumId") UUID curriculumId) {
                CurriculumDetailResponse result = service.getByIdWithDetails(curriculumId);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/goal/{goalId}")
        @Operation(summary = "Get curricula by goal", description = "Retrieve all curricula for a specific goal")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved curricula for goal")
        })
        public ResponseEntity<BaseResponse<List<CurriculumResponse>>> getByGoalId(
                        @Parameter(description = "Goal ID") @PathVariable("goalId") UUID goalId) {
                List<CurriculumResponse> result = service.getByGoalId(goalId);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @PostMapping("/filter")
        @Operation(summary = "Filter curricula", description = "Filter curricula with various criteria")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully filtered curricula")
        })
        public ResponseEntity<PagedResponse<CurriculumResponse>> filter(
                        @RequestBody CurriculumFilterRequest filterRequest) {
                Page<CurriculumResponse> result = service.filter(filterRequest);
                return ResponseEntity.ok(PagedResponse.of(result));
        }

        @PostMapping
        @Operation(summary = "Create a curriculum", description = "Create a new curriculum")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Curriculum created successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid request data")
        })
        public ResponseEntity<BaseResponse<CurriculumResponse>> create(
                        @Valid @RequestBody CurriculumCreateRequest request) {
                CurriculumResponse result = service.create(request);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(BaseResponse.ok(result, "Curriculum created successfully"));
        }

        @PostMapping("/batch")
        @Operation(summary = "Create multiple curricula", description = "Create multiple curricula in batch")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Curricula created successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid request data")
        })
        public ResponseEntity<BaseResponse<List<CurriculumResponse>>> createBatch(
                        @Valid @RequestBody List<CurriculumCreateRequest> requests) {
                List<CurriculumResponse> result = service.createBatch(requests);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(BaseResponse.ok(result, "Curricula created successfully"));
        }

        @PatchMapping("/{curriculumId}")
        @Operation(summary = "Update a curriculum", description = "Partially update an existing curriculum")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Curriculum updated successfully"),
                        @ApiResponse(responseCode = "404", description = "Curriculum not found"),
                        @ApiResponse(responseCode = "400", description = "Invalid request data")
        })
        public ResponseEntity<BaseResponse<CurriculumResponse>> partialUpdate(
                        @Parameter(description = "Curriculum ID") @PathVariable("curriculumId") UUID curriculumId,
                        @RequestBody CurriculumUpdateRequest request) {
                CurriculumResponse result = service.partialUpdate(curriculumId, request);
                return ResponseEntity.ok(BaseResponse.ok(result, "Curriculum updated successfully"));
        }

        @DeleteMapping("/{curriculumId}")
        @Operation(summary = "Delete a curriculum", description = "Soft delete a curriculum")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Curriculum deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "Curriculum not found")
        })
        public ResponseEntity<BaseResponse<Void>> softDelete(
                        @Parameter(description = "Curriculum ID") @PathVariable("curriculumId") UUID curriculumId) {
                service.softDelete(curriculumId);
                return ResponseEntity.ok(BaseResponse.ok("Curriculum deleted successfully"));
        }
}
