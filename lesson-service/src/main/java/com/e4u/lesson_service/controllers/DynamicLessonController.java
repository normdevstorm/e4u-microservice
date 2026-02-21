package com.e4u.lesson_service.controllers;

import com.e4u.lesson_service.models.request.DynamicLessonCreateRequest;
import com.e4u.lesson_service.models.request.DynamicLessonFilterRequest;
import com.e4u.lesson_service.models.request.DynamicLessonUpdateRequest;
import com.e4u.lesson_service.models.response.BaseResponse;
import com.e4u.lesson_service.models.response.DynamicLessonDetailResponse;
import com.e4u.lesson_service.models.response.DynamicLessonResponse;
import com.e4u.lesson_service.models.response.PagedResponse;
import com.e4u.lesson_service.models.response.UserVocabInstanceResponse;
import com.e4u.lesson_service.services.DynamicLessonService;
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
 * REST Controller for DynamicLesson CRUD operations.
 */
@RestController
@RequestMapping("/v1/dynamic-lessons")
@RequiredArgsConstructor
@Tag(name = "Dynamic Lessons", description = "APIs for managing dynamic lessons")
public class DynamicLessonController {

        private final DynamicLessonService service;

        // ==================== GET Operations ====================

        @GetMapping
        @Operation(summary = "Get all dynamic lessons", description = "Retrieve all dynamic lessons with pagination")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved dynamic lessons")
        })
        public ResponseEntity<PagedResponse<DynamicLessonResponse>> getAll(
                        @Parameter(name = "page", description = "Page number (0-indexed)", example = "0") @RequestParam(name = "page", defaultValue = "0") int page,
                        @Parameter(name = "size", description = "Page size", example = "20") @RequestParam(name = "size", defaultValue = "20") int size,
                        @Parameter(name = "sortBy", description = "Sort field", example = "createdAt") @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                        @Parameter(name = "sortDirection", description = "Sort direction (ASC/DESC)", example = "DESC") @RequestParam(name = "sortDirection", defaultValue = "DESC") String sortDirection) {
                Page<DynamicLessonResponse> result = service.getAll(page, size, sortBy, sortDirection);
                return ResponseEntity.ok(PagedResponse.of(result));
        }

        @GetMapping("/{id}")
        @Operation(summary = "Get dynamic lesson by ID", description = "Retrieve a specific dynamic lesson by its ID")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved dynamic lesson"),
                        @ApiResponse(responseCode = "404", description = "Dynamic lesson not found")
        })
        public ResponseEntity<BaseResponse<DynamicLessonResponse>> getById(
                        @Parameter(name = "id", description = "Dynamic lesson ID", example = "1b9d6bcd-bbfd-4b2d-9b5d-ab8dfbbd4bed") @PathVariable("id") UUID id) {
                DynamicLessonResponse result = service.getById(id);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/{id}/details")
        @Operation(summary = "Get dynamic lesson with details", description = "Retrieve a dynamic lesson with full vocab item details")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved dynamic lesson details"),
                        @ApiResponse(responseCode = "404", description = "Dynamic lesson not found")
        })
        public ResponseEntity<BaseResponse<DynamicLessonDetailResponse>> getByIdWithDetails(
                        @Parameter(name = "id", description = "Dynamic lesson ID", example = "1b9d6bcd-bbfd-4b2d-9b5d-ab8dfbbd4bed") @PathVariable("id") UUID id) {
                DynamicLessonDetailResponse result = service.getByIdWithDetails(id);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/user/{userId}")
        @Operation(summary = "Get dynamic lessons by user", description = "Retrieve all dynamic lessons for a specific user")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved dynamic lessons")
        })
        public ResponseEntity<BaseResponse<List<DynamicLessonResponse>>> getByUserId(
                        @Parameter(name = "userId", description = "User ID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("userId") UUID userId) {
                List<DynamicLessonResponse> result = service.getByUserId(userId);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/user/{userId}/paged")
        @Operation(summary = "Get dynamic lessons by user (paginated)", description = "Retrieve paginated dynamic lessons for a specific user")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved dynamic lessons")
        })
        public ResponseEntity<PagedResponse<DynamicLessonResponse>> getByUserIdPaged(
                        @Parameter(name = "userId", description = "User ID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("userId") UUID userId,
                        @Parameter(name = "page", description = "Page number (0-indexed)", example = "0") @RequestParam(name = "page", defaultValue = "0") int page,
                        @Parameter(name = "size", description = "Page size", example = "20") @RequestParam(name = "size", defaultValue = "20") int size,
                        @Parameter(name = "sortBy", description = "Sort field", example = "createdAt") @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                        @Parameter(name = "sortDirection", description = "Sort direction (ASC/DESC)", example = "DESC") @RequestParam(name = "sortDirection", defaultValue = "DESC") String sortDirection) {
                Page<DynamicLessonResponse> result = service.getByUserId(userId, page, size, sortBy, sortDirection);
                return ResponseEntity.ok(PagedResponse.of(result));
        }

        @GetMapping("/unit/{unitId}")
        @Operation(summary = "Get dynamic lessons by unit", description = "Retrieve all dynamic lessons for a specific unit (UserUnitState)")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved dynamic lessons")
        })
        public ResponseEntity<BaseResponse<List<DynamicLessonResponse>>> getByUnitId(
                        @Parameter(name = "unitId", description = "UserUnitState ID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479") @PathVariable("unitId") UUID unitId) {
                List<DynamicLessonResponse> result = service.getByUnitId(unitId);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/unit/{unitId}/paged")
        @Operation(summary = "Get dynamic lessons by unit (paginated)", description = "Retrieve paginated dynamic lessons for a specific unit")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved dynamic lessons")
        })
        public ResponseEntity<PagedResponse<DynamicLessonResponse>> getByUnitIdPaged(
                        @Parameter(name = "unitId", description = "UserUnitState ID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479") @PathVariable("unitId") UUID unitId,
                        @Parameter(name = "page", description = "Page number (0-indexed)", example = "0") @RequestParam(name = "page", defaultValue = "0") int page,
                        @Parameter(name = "size", description = "Page size", example = "20") @RequestParam(name = "size", defaultValue = "20") int size,
                        @Parameter(name = "sortBy", description = "Sort field", example = "createdAt") @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                        @Parameter(name = "sortDirection", description = "Sort direction (ASC/DESC)", example = "DESC") @RequestParam(name = "sortDirection", defaultValue = "DESC") String sortDirection) {
                Page<DynamicLessonResponse> result = service.getByUnitId(unitId, page, size, sortBy, sortDirection);
                return ResponseEntity.ok(PagedResponse.of(result));
        }

        @PostMapping("/filter")
        @Operation(summary = "Filter dynamic lessons", description = "Filter dynamic lessons with various criteria")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully filtered dynamic lessons")
        })
        public ResponseEntity<PagedResponse<DynamicLessonResponse>> filter(
                        @RequestBody DynamicLessonFilterRequest filterRequest) {
                Page<DynamicLessonResponse> result = service.filter(filterRequest);
                return ResponseEntity.ok(PagedResponse.of(result));
        }

        @GetMapping("/{id}/vocab-items")
        @Operation(summary = "Get all vocab items by dynamic lesson", description = "Retrieve all vocabulary instances associated with a dynamic lesson")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved vocab items"),
                        @ApiResponse(responseCode = "404", description = "Dynamic lesson not found")
        })
        public ResponseEntity<BaseResponse<List<UserVocabInstanceResponse>>> getAllVocabByDynamicLesson(
                        @Parameter(name = "id", description = "Dynamic lesson ID", example = "1b9d6bcd-bbfd-4b2d-9b5d-ab8dfbbd4bed") @PathVariable("id") UUID id) {
                List<UserVocabInstanceResponse> result = service.getAllVocabByDynamicLesson(id);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        // ==================== CREATE Operations ====================

        @PostMapping
        @Operation(summary = "Create dynamic lesson", description = "Create a new dynamic lesson with a list of vocab instances")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Successfully created dynamic lesson"),
                        @ApiResponse(responseCode = "400", description = "Invalid request data"),
                        @ApiResponse(responseCode = "404", description = "Vocab instance or UserUnitState not found")
        })
        public ResponseEntity<BaseResponse<DynamicLessonResponse>> create(
                        @Valid @RequestBody DynamicLessonCreateRequest request) {
                DynamicLessonResponse result = service.create(request);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(BaseResponse.ok(result, "Dynamic lesson created successfully"));
        }

        // ==================== UPDATE Operations ====================

        @PatchMapping("/{id}")
        @Operation(summary = "Partially update dynamic lesson", description = "Update specific fields of a dynamic lesson")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully updated dynamic lesson"),
                        @ApiResponse(responseCode = "404", description = "Dynamic lesson not found"),
                        @ApiResponse(responseCode = "400", description = "Invalid request data")
        })
        public ResponseEntity<BaseResponse<DynamicLessonResponse>> partialUpdate(
                        @Parameter(name = "id", description = "Dynamic lesson ID", example = "1b9d6bcd-bbfd-4b2d-9b5d-ab8dfbbd4bed") @PathVariable("id") UUID id,
                        @RequestBody DynamicLessonUpdateRequest request) {
                DynamicLessonResponse result = service.partialUpdate(id, request);
                return ResponseEntity.ok(BaseResponse.ok(result, "Dynamic lesson updated successfully"));
        }

        // ==================== DELETE Operations ====================

        @DeleteMapping("/{id}")
        @Operation(summary = "Soft delete dynamic lesson", description = "Soft delete a dynamic lesson (marks as deleted)")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully deleted dynamic lesson"),
                        @ApiResponse(responseCode = "404", description = "Dynamic lesson not found")
        })
        public ResponseEntity<BaseResponse<Void>> softDelete(
                        @Parameter(name = "id", description = "Dynamic lesson ID", example = "1b9d6bcd-bbfd-4b2d-9b5d-ab8dfbbd4bed") @PathVariable("id") UUID id) {
                service.softDelete(id);
                return ResponseEntity.ok(BaseResponse.ok("Dynamic lesson deleted successfully"));
        }
}
