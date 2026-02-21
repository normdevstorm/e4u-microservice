package com.e4u.curriculum_service.controllers;

import com.e4u.curriculum_service.models.request.GlobalDictionaryCreateRequest;
import com.e4u.curriculum_service.models.request.GlobalDictionaryFilterRequest;
import com.e4u.curriculum_service.models.request.GlobalDictionaryUpdateRequest;
import com.e4u.curriculum_service.models.response.BaseResponse;
import com.e4u.curriculum_service.models.response.GlobalDictionaryResponse;
import com.e4u.curriculum_service.models.response.PagedResponse;
import com.e4u.curriculum_service.services.GlobalDictionaryService;
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
 * REST Controller for managing Global Dictionary.
 */
@RestController
@RequestMapping("/v1/dictionary")
@RequiredArgsConstructor
@Tag(name = "Global Dictionary", description = "APIs for managing the global vocabulary dictionary")
public class GlobalDictionaryController {

    private final GlobalDictionaryService service;

    @GetMapping
    @Operation(summary = "Get all dictionary entries", description = "Retrieve all dictionary entries with pagination")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved dictionary entries")
    })
    public ResponseEntity<PagedResponse<GlobalDictionaryResponse>> getAll(
            @Parameter(name = "page", description = "Page number (0-indexed)", example = "0") @RequestParam(name = "page", defaultValue = "0") int page,
            @Parameter(name = "size", description = "Page size", example = "20") @RequestParam(name = "size", defaultValue = "20") int size,
            @Parameter(name = "sortBy", description = "Sort field", example = "lemma") @RequestParam(name = "sortBy", defaultValue = "lemma") String sortBy,
            @Parameter(name = "sortDirection", description = "Sort direction (ASC/DESC)", example = "ASC") @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection) {
        Page<GlobalDictionaryResponse> result = service.getAll(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(PagedResponse.of(result));
    }

    @GetMapping("/{wordId}")
    @Operation(summary = "Get dictionary entry by ID", description = "Retrieve a specific dictionary entry by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the dictionary entry"),
            @ApiResponse(responseCode = "404", description = "Dictionary entry not found")
    })
    public ResponseEntity<BaseResponse<GlobalDictionaryResponse>> getById(
            @Parameter(description = "Word ID") @PathVariable("wordId") UUID wordId) {
        GlobalDictionaryResponse result = service.getById(wordId);
        return ResponseEntity.ok(BaseResponse.ok(result));
    }

    @GetMapping("/{wordId}/translations")
    @Operation(summary = "Get dictionary entry with translations", description = "Retrieve a dictionary entry with all its translations")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved dictionary entry with translations"),
            @ApiResponse(responseCode = "404", description = "Dictionary entry not found")
    })
    public ResponseEntity<BaseResponse<GlobalDictionaryResponse>> getByIdWithTranslations(
            @Parameter(description = "Word ID") @PathVariable("wordId") UUID wordId) {
        GlobalDictionaryResponse result = service.getByIdWithTranslations(wordId);
        return ResponseEntity.ok(BaseResponse.ok(result));
    }

    @GetMapping("/search")
    @Operation(summary = "Search dictionary", description = "Search dictionary entries by keyword")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully searched dictionary entries")
    })
    public ResponseEntity<PagedResponse<GlobalDictionaryResponse>> search(
            @Parameter(description = "Search keyword") @RequestParam("keyword") String keyword,
            @Parameter(name = "page", description = "Page number", example = "0") @RequestParam(name = "page", defaultValue = "0") int page,
            @Parameter(name = "size", description = "Page size", example = "20") @RequestParam(name = "size", defaultValue = "20") int size) {
        Page<GlobalDictionaryResponse> result = service.search(keyword, page, size);
        return ResponseEntity.ok(PagedResponse.of(result));
    }

    @PostMapping("/filter")
    @Operation(summary = "Filter dictionary entries", description = "Filter dictionary entries with various criteria")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully filtered dictionary entries")
    })
    public ResponseEntity<PagedResponse<GlobalDictionaryResponse>> filter(
            @RequestBody GlobalDictionaryFilterRequest filterRequest) {
        Page<GlobalDictionaryResponse> result = service.filter(filterRequest);
        return ResponseEntity.ok(PagedResponse.of(result));
    }

    @PostMapping
    @Operation(summary = "Create a dictionary entry", description = "Create a new dictionary entry")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Dictionary entry created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<BaseResponse<GlobalDictionaryResponse>> create(
            @Valid @RequestBody GlobalDictionaryCreateRequest request) {
        GlobalDictionaryResponse result = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.ok(result, "Dictionary entry created successfully"));
    }

    @PostMapping("/batch")
    @Operation(summary = "Create multiple dictionary entries", description = "Create multiple dictionary entries in batch")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Dictionary entries created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<BaseResponse<List<GlobalDictionaryResponse>>> createBatch(
            @Valid @RequestBody List<GlobalDictionaryCreateRequest> requests) {
        List<GlobalDictionaryResponse> result = service.createBatch(requests);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.ok(result, "Dictionary entries created successfully"));
    }

    @PatchMapping("/{wordId}")
    @Operation(summary = "Update a dictionary entry", description = "Partially update an existing dictionary entry")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dictionary entry updated successfully"),
            @ApiResponse(responseCode = "404", description = "Dictionary entry not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<BaseResponse<GlobalDictionaryResponse>> partialUpdate(
            @Parameter(description = "Word ID") @PathVariable("wordId") UUID wordId,
            @RequestBody GlobalDictionaryUpdateRequest request) {
        GlobalDictionaryResponse result = service.partialUpdate(wordId, request);
        return ResponseEntity.ok(BaseResponse.ok(result, "Dictionary entry updated successfully"));
    }

    @DeleteMapping("/{wordId}")
    @Operation(summary = "Delete a dictionary entry", description = "Soft delete a dictionary entry")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dictionary entry deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Dictionary entry not found")
    })
    public ResponseEntity<BaseResponse<Void>> softDelete(
            @Parameter(description = "Word ID") @PathVariable("wordId") UUID wordId) {
        service.softDelete(wordId);
        return ResponseEntity.ok(BaseResponse.ok("Dictionary entry deleted successfully"));
    }
}
