package com.e4u.learning_service.controllers;

import com.e4u.learning_service.common.utils.SecurityUtil;
import com.e4u.learning_service.dtos.request.CurriculumUnitCreateRequest;
import com.e4u.learning_service.dtos.request.CurriculumUnitFilterRequest;
import com.e4u.learning_service.dtos.request.CurriculumUnitUpdateRequest;
import com.e4u.learning_service.dtos.response.BaseResponse;
import com.e4u.learning_service.dtos.response.CurriculumUnitDetailResponse;
import com.e4u.learning_service.dtos.response.CurriculumUnitResponse;
import com.e4u.learning_service.dtos.response.PagedResponse;
import com.e4u.learning_service.dtos.response.WordContextResponse;
import com.e4u.learning_service.services.CurriculumUnitService;
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
 * REST Controller for managing Curriculum Units.
 */
@RestController
@RequestMapping("/v1/curriculum-units")
@RequiredArgsConstructor
@Tag(name = "Curriculum Units", description = "APIs for managing curriculum units")
public class CurriculumUnitController {

        private final CurriculumUnitService service;

        @GetMapping
        @Operation(summary = "Get all curriculum units", description = "Retrieve all curriculum units with pagination")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved curriculum units")
        })
        public ResponseEntity<PagedResponse<CurriculumUnitResponse>> getAll(
                        @Parameter(name = "page", description = "Page number (0-indexed)", example = "0") @RequestParam(name = "page", defaultValue = "0") int page,
                        @Parameter(name = "size", description = "Page size", example = "20") @RequestParam(name = "size", defaultValue = "20") int size,
                        @Parameter(name = "sortBy", description = "Sort field", example = "defaultOrder") @RequestParam(name = "sortBy", defaultValue = "defaultOrder") String sortBy,
                        @Parameter(name = "sortDirection", description = "Sort direction (ASC/DESC)", example = "ASC") @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection) {
                Page<CurriculumUnitResponse> result = service.getAll(page, size, sortBy, sortDirection);
                return ResponseEntity.ok(PagedResponse.of(result));
        }

        @GetMapping("/{unitId}")
        @Operation(summary = "Get curriculum unit by ID", description = "Retrieve a specific curriculum unit by its ID")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved the curriculum unit"),
                        @ApiResponse(responseCode = "404", description = "Curriculum unit not found")
        })
        public ResponseEntity<BaseResponse<CurriculumUnitResponse>> getById(
                        @Parameter(description = "Unit ID") @PathVariable("unitId") UUID unitId) {
                CurriculumUnitResponse result = service.getById(unitId);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/{unitId}/details")
        @Operation(summary = "Get curriculum unit with details", description = "Retrieve a curriculum unit with its base words")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved curriculum unit with details"),
                        @ApiResponse(responseCode = "404", description = "Curriculum unit not found")
        })
        public ResponseEntity<BaseResponse<CurriculumUnitDetailResponse>> getByIdWithDetails(
                        @Parameter(description = "Unit ID") @PathVariable("unitId") UUID unitId) {
                CurriculumUnitDetailResponse result = service.getByIdWithDetails(unitId);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/{unitId}/words")
        @Operation(summary = "Get words for a unit", description = "Retrieve all word context templates for a specific unit")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved words for unit"),
                        @ApiResponse(responseCode = "404", description = "Curriculum unit not found")
        })
        public ResponseEntity<BaseResponse<List<WordContextResponse>>> getWordsByUnitId(
                        @Parameter(description = "Unit ID") @PathVariable("unitId") UUID unitId,
                        // @Parameter(description = "User ID (optional, for user-specific contexts)")
                        @RequestParam(name = "userId", required = false) UUID userId) {
                List<WordContextResponse> result;
                if (userId == null) {
                        userId = SecurityUtil.getCurrentUserId();
                }
                result = service.getWordsByUnitIdForUser(unitId, userId);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/curriculum/{curriculumId}")
        @Operation(summary = "Get units by curriculum", description = "Retrieve all units for a specific curriculum")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved units for curriculum")
        })
        public ResponseEntity<BaseResponse<List<CurriculumUnitResponse>>> getByCurriculumId(
                        @Parameter(description = "Curriculum ID") @PathVariable("curriculumId") UUID curriculumId) {
                List<CurriculumUnitResponse> result = service.getByCurriculumId(curriculumId);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @PostMapping("/filter")
        @Operation(summary = "Filter curriculum units", description = "Filter curriculum units with various criteria")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully filtered curriculum units")
        })
        public ResponseEntity<PagedResponse<CurriculumUnitResponse>> filter(
                        @RequestBody CurriculumUnitFilterRequest filterRequest) {
                Page<CurriculumUnitResponse> result = service.filter(filterRequest);
                return ResponseEntity.ok(PagedResponse.of(result));
        }

        @PostMapping
        @Operation(summary = "Create a curriculum unit", description = "Create a new curriculum unit")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Curriculum unit created successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid request data")
        })
        public ResponseEntity<BaseResponse<CurriculumUnitResponse>> create(
                        @Valid @RequestBody CurriculumUnitCreateRequest request) {
                CurriculumUnitResponse result = service.create(request);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(BaseResponse.ok(result, "Curriculum unit created successfully"));
        }

        @PostMapping("/batch")
        @Operation(summary = "Create multiple curriculum units", description = "Create multiple curriculum units in batch")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Curriculum units created successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid request data")
        })
        public ResponseEntity<BaseResponse<List<CurriculumUnitResponse>>> createBatch(
                        @Valid @RequestBody List<CurriculumUnitCreateRequest> requests) {
                List<CurriculumUnitResponse> result = service.createBatch(requests);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(BaseResponse.ok(result, "Curriculum units created successfully"));
        }

        @PatchMapping("/{unitId}")
        @Operation(summary = "Update a curriculum unit", description = "Partially update an existing curriculum unit")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Curriculum unit updated successfully"),
                        @ApiResponse(responseCode = "404", description = "Curriculum unit not found"),
                        @ApiResponse(responseCode = "400", description = "Invalid request data")
        })
        public ResponseEntity<BaseResponse<CurriculumUnitResponse>> partialUpdate(
                        @Parameter(description = "Unit ID") @PathVariable("unitId") UUID unitId,
                        @RequestBody CurriculumUnitUpdateRequest request) {
                CurriculumUnitResponse result = service.partialUpdate(unitId, request);
                return ResponseEntity.ok(BaseResponse.ok(result, "Curriculum unit updated successfully"));
        }

        @DeleteMapping("/{unitId}")
        @Operation(summary = "Delete a curriculum unit", description = "Soft delete a curriculum unit")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Curriculum unit deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "Curriculum unit not found")
        })
        public ResponseEntity<BaseResponse<Void>> softDelete(
                        @Parameter(description = "Unit ID") @PathVariable("unitId") UUID unitId) {
                service.softDelete(unitId);
                return ResponseEntity.ok(BaseResponse.ok("Curriculum unit deleted successfully"));
        }
}
