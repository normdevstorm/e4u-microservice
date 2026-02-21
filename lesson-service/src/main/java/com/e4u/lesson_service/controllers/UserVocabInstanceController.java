package com.e4u.lesson_service.controllers;

import com.e4u.lesson_service.models.request.UserVocabInstanceCreateRequest;
import com.e4u.lesson_service.models.request.UserVocabInstanceFilterRequest;
import com.e4u.lesson_service.models.request.UserVocabInstanceUpdateRequest;
import com.e4u.lesson_service.models.response.BaseResponse;
import com.e4u.lesson_service.models.response.PagedResponse;
import com.e4u.lesson_service.models.response.UserVocabInstanceResponse;
import com.e4u.lesson_service.services.UserVocabInstanceService;
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
 * REST Controller for UserVocabInstance CRUD operations.
 */
@RestController
@RequestMapping("/v1/vocab-instances")
@RequiredArgsConstructor
@Tag(name = "User Vocabulary Instances", description = "APIs for managing user vocabulary instances")
public class UserVocabInstanceController {

        private final UserVocabInstanceService service;

        // ==================== GET Operations ====================

        @GetMapping
        @Operation(summary = "Get all vocabulary instances", description = "Retrieve all vocabulary instances with pagination")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved vocabulary instances")
        })
        public ResponseEntity<PagedResponse<UserVocabInstanceResponse>> getAll(
                        @Parameter(name = "page", description = "Page number (0-indexed)", example = "0") @RequestParam(name = "page", defaultValue = "0") int page,
                        @Parameter(name = "size", description = "Page size", example = "20") @RequestParam(name = "size", defaultValue = "20") int size,
                        @Parameter(name = "sortBy", description = "Sort field", example = "createdAt") @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                        @Parameter(name = "sortDirection", description = "Sort direction (ASC/DESC)", example = "DESC") @RequestParam(name = "sortDirection", defaultValue = "DESC") String sortDirection) {
                Page<UserVocabInstanceResponse> result = service.getAll(page, size, sortBy, sortDirection);
                return ResponseEntity.ok(PagedResponse.of(result));
        }

        @GetMapping("/{id}")
        @Operation(summary = "Get vocabulary instance by ID", description = "Retrieve a specific vocabulary instance by its ID")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved vocabulary instance"),
                        @ApiResponse(responseCode = "404", description = "Vocabulary instance not found")
        })
        public ResponseEntity<BaseResponse<UserVocabInstanceResponse>> getById(
                        @Parameter(name = "id", description = "Vocabulary instance ID", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11") @PathVariable("id") UUID id) {
                UserVocabInstanceResponse result = service.getById(id);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/user/{userId}")
        @Operation(summary = "Get vocabulary instances by user", description = "Retrieve all vocabulary instances for a specific user")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved vocabulary instances")
        })
        public ResponseEntity<BaseResponse<List<UserVocabInstanceResponse>>> getByUserId(
                        @Parameter(name = "userId", description = "User ID", example = "550e8400-e29b-41d4-a716-446655440000") @PathVariable("userId") UUID userId) {
                List<UserVocabInstanceResponse> result = service.getByUserId(userId);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @PostMapping("/filter")
        @Operation(summary = "Filter vocabulary instances", description = "Filter vocabulary instances with various criteria")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully filtered vocabulary instances")
        })
        public ResponseEntity<PagedResponse<UserVocabInstanceResponse>> filter(
                        @RequestBody UserVocabInstanceFilterRequest filterRequest) {
                Page<UserVocabInstanceResponse> result = service.filter(filterRequest);
                return ResponseEntity.ok(PagedResponse.of(result));
        }

        // ==================== CREATE Operations ====================

        @PostMapping
        @Operation(summary = "Create vocabulary instance", description = "Create a new vocabulary instance")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Successfully created vocabulary instance"),
                        @ApiResponse(responseCode = "400", description = "Invalid request data")
        })
        public ResponseEntity<BaseResponse<UserVocabInstanceResponse>> create(
                        @Valid @RequestBody UserVocabInstanceCreateRequest request) {
                UserVocabInstanceResponse result = service.create(request);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(BaseResponse.ok(result, "Vocabulary instance created successfully"));
        }

        @PostMapping("/batch")
        @Operation(summary = "Create multiple vocabulary instances", description = "Create multiple vocabulary instances in a batch")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Successfully created vocabulary instances"),
                        @ApiResponse(responseCode = "400", description = "Invalid request data")
        })
        public ResponseEntity<BaseResponse<List<UserVocabInstanceResponse>>> createBatch(
                        @Valid @RequestBody List<UserVocabInstanceCreateRequest> requests) {
                List<UserVocabInstanceResponse> result = service.createBatch(requests);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(BaseResponse.ok(result, "Created " + result.size() + " vocabulary instances"));
        }

        // ==================== UPDATE Operations ====================

        @PatchMapping("/{id}")
        @Operation(summary = "Partially update vocabulary instance", description = "Update specific fields of a vocabulary instance")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully updated vocabulary instance"),
                        @ApiResponse(responseCode = "404", description = "Vocabulary instance not found"),
                        @ApiResponse(responseCode = "400", description = "Invalid request data")
        })
        public ResponseEntity<BaseResponse<UserVocabInstanceResponse>> partialUpdate(
                        @Parameter(name = "id", description = "Vocabulary instance ID", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11") @PathVariable("id") UUID id,
                        @RequestBody UserVocabInstanceUpdateRequest request) {
                UserVocabInstanceResponse result = service.partialUpdate(id, request);
                return ResponseEntity.ok(BaseResponse.ok(result, "Vocabulary instance updated successfully"));
        }

        // ==================== DELETE Operations ====================

        @DeleteMapping("/{id}")
        @Operation(summary = "Soft delete vocabulary instance", description = "Soft delete a vocabulary instance (marks as deleted)")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully deleted vocabulary instance"),
                        @ApiResponse(responseCode = "404", description = "Vocabulary instance not found")
        })
        public ResponseEntity<BaseResponse<Void>> softDelete(
                        @Parameter(name = "id", description = "Vocabulary instance ID", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11") @PathVariable("id") UUID id) {
                service.softDelete(id);
                return ResponseEntity.ok(BaseResponse.ok("Vocabulary instance deleted successfully"));
        }

        @DeleteMapping("/batch")
        @Operation(summary = "Soft delete multiple vocabulary instances", description = "Soft delete multiple vocabulary instances")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully deleted vocabulary instances")
        })
        public ResponseEntity<BaseResponse<Void>> softDeleteBatch(
                        @RequestBody List<UUID> ids) {
                service.softDeleteBatch(ids);
                return ResponseEntity.ok(BaseResponse.ok("Deleted " + ids.size() + " vocabulary instances"));
        }
}
