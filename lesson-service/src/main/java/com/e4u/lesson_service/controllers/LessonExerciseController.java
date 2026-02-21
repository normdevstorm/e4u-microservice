package com.e4u.lesson_service.controllers;

import com.e4u.lesson_service.models.request.LessonExerciseCreateRequest;
import com.e4u.lesson_service.models.request.LessonExerciseFilterRequest;
import com.e4u.lesson_service.models.request.LessonExerciseUpdateRequest;
import com.e4u.lesson_service.models.response.BaseResponse;
import com.e4u.lesson_service.models.response.LessonExerciseDetailResponse;
import com.e4u.lesson_service.models.response.LessonExerciseResponse;
import com.e4u.lesson_service.models.response.PagedResponse;
import com.e4u.lesson_service.services.LessonExerciseService;
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
 * REST Controller for LessonExercise CRUD operations.
 */
@RestController
@RequestMapping("/v1/lesson-exercises")
@RequiredArgsConstructor
@Tag(name = "Lesson Exercises", description = "APIs for managing lesson exercises")
public class LessonExerciseController {

        private final LessonExerciseService service;

        // ==================== GET Operations ====================

        @GetMapping
        @Operation(summary = "Get all lesson exercises", description = "Retrieve all lesson exercises with pagination")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved lesson exercises")
        })
        public ResponseEntity<PagedResponse<LessonExerciseResponse>> getAll(
                        @Parameter(name = "page", description = "Page number (0-indexed)", example = "0") @RequestParam(name = "page", defaultValue = "0") int page,
                        @Parameter(name = "size", description = "Page size", example = "20") @RequestParam(name = "size", defaultValue = "20") int size,
                        @Parameter(name = "sortBy", description = "Sort field", example = "sequenceOrder") @RequestParam(name = "sortBy", defaultValue = "sequenceOrder") String sortBy,
                        @Parameter(name = "sortDirection", description = "Sort direction (ASC/DESC)", example = "ASC") @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection) {
                Page<LessonExerciseResponse> result = service.getAll(page, size, sortBy, sortDirection);
                return ResponseEntity.ok(PagedResponse.of(result));
        }

        @GetMapping("/{id}")
        @Operation(summary = "Get lesson exercise by ID", description = "Retrieve a specific lesson exercise by its ID")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved lesson exercise"),
                        @ApiResponse(responseCode = "404", description = "Lesson exercise not found")
        })
        public ResponseEntity<BaseResponse<LessonExerciseResponse>> getById(
                        @Parameter(name = "id", description = "Lesson exercise ID", example = "e1a2b3c4-d5e6-4f7a-8b9c-0d1e2f3a4b5c") @PathVariable("id") UUID id) {
                LessonExerciseResponse result = service.getById(id);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/{id}/details")
        @Operation(summary = "Get lesson exercise with details", description = "Retrieve a lesson exercise with full vocab instance details")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved lesson exercise details"),
                        @ApiResponse(responseCode = "404", description = "Lesson exercise not found")
        })
        public ResponseEntity<BaseResponse<LessonExerciseDetailResponse>> getByIdWithDetails(
                        @Parameter(name = "id", description = "Lesson exercise ID", example = "e1a2b3c4-d5e6-4f7a-8b9c-0d1e2f3a4b5c") @PathVariable("id") UUID id) {
                LessonExerciseDetailResponse result = service.getByIdWithDetails(id);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/vocab/{vocabId}")
        @Operation(summary = "Get exercises by vocab ID", description = "Retrieve all exercises for a specific vocabulary instance")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved exercises")
        })
        public ResponseEntity<BaseResponse<List<LessonExerciseResponse>>> getByVocabId(
                        @Parameter(name = "vocabId", description = "Vocabulary instance ID", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11") @PathVariable("vocabId") UUID vocabId) {
                List<LessonExerciseResponse> result = service.getByVocabId(vocabId);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/vocab/{vocabId}/paged")
        @Operation(summary = "Get exercises by vocab ID (paginated)", description = "Retrieve paginated exercises for a vocabulary instance")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved exercises")
        })
        public ResponseEntity<PagedResponse<LessonExerciseResponse>> getByVocabIdPaged(
                        @Parameter(name = "vocabId", description = "Vocabulary instance ID", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11") @PathVariable("vocabId") UUID vocabId,
                        @Parameter(name = "page", description = "Page number (0-indexed)", example = "0") @RequestParam(name = "page", defaultValue = "0") int page,
                        @Parameter(name = "size", description = "Page size", example = "20") @RequestParam(name = "size", defaultValue = "20") int size,
                        @Parameter(name = "sortBy", description = "Sort field", example = "sequenceOrder") @RequestParam(name = "sortBy", defaultValue = "sequenceOrder") String sortBy,
                        @Parameter(name = "sortDirection", description = "Sort direction (ASC/DESC)", example = "ASC") @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection) {
                Page<LessonExerciseResponse> result = service.getByVocabId(vocabId, page, size, sortBy, sortDirection);
                return ResponseEntity.ok(PagedResponse.of(result));
        }

        @GetMapping("/lesson/{lessonId}")
        @Operation(summary = "Get exercises by lesson ID", description = "Retrieve all exercises for a specific lesson")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved exercises")
        })
        public ResponseEntity<BaseResponse<List<LessonExerciseResponse>>> getByLessonId(
                        @Parameter(name = "lessonId", description = "Dynamic Lesson ID", example = "1b9d6bcd-bbfd-4b2d-9b5d-ab8dfbbd4bed") @PathVariable("lessonId") UUID lessonId) {
                List<LessonExerciseResponse> result = service.getByLessonId(lessonId);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/lesson/{lessonId}/paged")
        @Operation(summary = "Get exercises by lesson ID (paginated)", description = "Retrieve paginated exercises for a lesson")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved exercises")
        })
        public ResponseEntity<PagedResponse<LessonExerciseResponse>> getByLessonIdPaged(
                        @Parameter(name = "lessonId", description = "Dynamic Lesson ID", example = "1b9d6bcd-bbfd-4b2d-9b5d-ab8dfbbd4bed") @PathVariable("lessonId") UUID lessonId,
                        @Parameter(name = "page", description = "Page number (0-indexed)", example = "0") @RequestParam(name = "page", defaultValue = "0") int page,
                        @Parameter(name = "size", description = "Page size", example = "20") @RequestParam(name = "size", defaultValue = "20") int size,
                        @Parameter(name = "sortBy", description = "Sort field", example = "sequenceOrder") @RequestParam(name = "sortBy", defaultValue = "sequenceOrder") String sortBy,
                        @Parameter(name = "sortDirection", description = "Sort direction (ASC/DESC)", example = "ASC") @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection) {
                Page<LessonExerciseResponse> result = service.getByLessonId(lessonId, page, size, sortBy,
                                sortDirection);
                return ResponseEntity.ok(PagedResponse.of(result));
        }

        @GetMapping("/unit/{unitId}")
        @Operation(summary = "Get exercises by unit ID", description = "Retrieve all exercises for a specific unit (UserUnitState)")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved exercises")
        })
        public ResponseEntity<BaseResponse<List<LessonExerciseResponse>>> getByUnitId(
                        @Parameter(name = "unitId", description = "UserUnitState ID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479") @PathVariable("unitId") UUID unitId) {
                List<LessonExerciseResponse> result = service.getByUnitId(unitId);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/unit/{unitId}/paged")
        @Operation(summary = "Get exercises by unit ID (paginated)", description = "Retrieve paginated exercises for a unit")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved exercises")
        })
        public ResponseEntity<PagedResponse<LessonExerciseResponse>> getByUnitIdPaged(
                        @Parameter(name = "unitId", description = "UserUnitState ID", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479") @PathVariable("unitId") UUID unitId,
                        @Parameter(name = "page", description = "Page number (0-indexed)", example = "0") @RequestParam(name = "page", defaultValue = "0") int page,
                        @Parameter(name = "size", description = "Page size", example = "20") @RequestParam(name = "size", defaultValue = "20") int size,
                        @Parameter(name = "sortBy", description = "Sort field", example = "sequenceOrder") @RequestParam(name = "sortBy", defaultValue = "sequenceOrder") String sortBy,
                        @Parameter(name = "sortDirection", description = "Sort direction (ASC/DESC)", example = "ASC") @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection) {
                Page<LessonExerciseResponse> result = service.getByUnitId(unitId, page, size, sortBy, sortDirection);
                return ResponseEntity.ok(PagedResponse.of(result));
        }

        @PostMapping("/filter")
        @Operation(summary = "Filter lesson exercises", description = "Filter lesson exercises with various criteria")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully filtered lesson exercises")
        })
        public ResponseEntity<PagedResponse<LessonExerciseResponse>> filter(
                        @RequestBody LessonExerciseFilterRequest filterRequest) {
                Page<LessonExerciseResponse> result = service.filter(filterRequest);
                return ResponseEntity.ok(PagedResponse.of(result));
        }

        // ==================== CREATE Operations ====================

        @PostMapping("/batch")
        @Operation(summary = "Create lesson exercises (batch)", description = "Create multiple lesson exercises in a batch")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Successfully created lesson exercises"),
                        @ApiResponse(responseCode = "400", description = "Invalid request data"),
                        @ApiResponse(responseCode = "404", description = "Lesson or vocab instance not found")
        })
        public ResponseEntity<BaseResponse<List<LessonExerciseResponse>>> createBatch(
                        @Valid @RequestBody List<LessonExerciseCreateRequest> requests) {
                List<LessonExerciseResponse> result = service.createBatch(requests);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(BaseResponse.ok(result, "Created " + result.size() + " lesson exercises"));
        }

        // ==================== UPDATE Operations ====================

        @PatchMapping("/{id}")
        @Operation(summary = "Partially update lesson exercise", description = "Update specific fields of a lesson exercise")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully updated lesson exercise"),
                        @ApiResponse(responseCode = "404", description = "Lesson exercise not found"),
                        @ApiResponse(responseCode = "400", description = "Invalid request data")
        })
        public ResponseEntity<BaseResponse<LessonExerciseResponse>> partialUpdate(
                        @Parameter(name = "id", description = "Lesson exercise ID", example = "e1a2b3c4-d5e6-4f7a-8b9c-0d1e2f3a4b5c") @PathVariable("id") UUID id,
                        @RequestBody LessonExerciseUpdateRequest request) {
                LessonExerciseResponse result = service.partialUpdate(id, request);
                return ResponseEntity.ok(BaseResponse.ok(result, "Lesson exercise updated successfully"));
        }

        // ==================== DELETE Operations ====================

        @DeleteMapping("/{id}")
        @Operation(summary = "Soft delete lesson exercise", description = "Soft delete a lesson exercise (marks as deleted)")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully deleted lesson exercise"),
                        @ApiResponse(responseCode = "404", description = "Lesson exercise not found")
        })
        public ResponseEntity<BaseResponse<Void>> softDelete(
                        @Parameter(name = "id", description = "Lesson exercise ID", example = "e1a2b3c4-d5e6-4f7a-8b9c-0d1e2f3a4b5c") @PathVariable("id") UUID id) {
                service.softDelete(id);
                return ResponseEntity.ok(BaseResponse.ok("Lesson exercise deleted successfully"));
        }

        @DeleteMapping("/batch")
        @Operation(summary = "Soft delete multiple lesson exercises", description = "Soft delete multiple lesson exercises")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully deleted lesson exercises")
        })
        public ResponseEntity<BaseResponse<Void>> softDeleteBatch(
                        @RequestBody List<UUID> ids) {
                service.softDeleteBatch(ids);
                return ResponseEntity.ok(BaseResponse.ok("Deleted " + ids.size() + " lesson exercises"));
        }
}
