package com.e4u.curriculum_service.controllers;

import com.e4u.curriculum_service.models.request.TranslationDictCreateRequest;
import com.e4u.curriculum_service.models.request.TranslationDictFilterRequest;
import com.e4u.curriculum_service.models.request.TranslationDictUpdateRequest;
import com.e4u.curriculum_service.models.response.BaseResponse;
import com.e4u.curriculum_service.models.response.PagedResponse;
import com.e4u.curriculum_service.models.response.TranslationDictResponse;
import com.e4u.curriculum_service.services.TranslationDictService;
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
 * REST Controller for managing Translation Dictionary.
 */
@RestController
@RequestMapping("/v1/translations")
@RequiredArgsConstructor
@Tag(name = "Translation Dictionary", description = "APIs for managing word translations")
public class TranslationDictController {

        private final TranslationDictService service;

        @GetMapping
        @Operation(summary = "Get all translations", description = "Retrieve all translations with pagination")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved translations")
        })
        public ResponseEntity<PagedResponse<TranslationDictResponse>> getAll(
                        @Parameter(name = "page", description = "Page number (0-indexed)", example = "0") @RequestParam(name = "page", defaultValue = "0") int page,
                        @Parameter(name = "size", description = "Page size", example = "20") @RequestParam(name = "size", defaultValue = "20") int size,
                        @Parameter(name = "sortBy", description = "Sort field", example = "id") @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                        @Parameter(name = "sortDirection", description = "Sort direction (ASC/DESC)", example = "ASC") @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection) {
                Page<TranslationDictResponse> result = service.getAll(page, size, sortBy, sortDirection);
                return ResponseEntity.ok(PagedResponse.of(result));
        }

        @GetMapping("/{id}")
        @Operation(summary = "Get translation by ID", description = "Retrieve a specific translation by its ID")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved the translation"),
                        @ApiResponse(responseCode = "404", description = "Translation not found")
        })
        public ResponseEntity<BaseResponse<TranslationDictResponse>> getById(
                        @Parameter(description = "Translation ID") @PathVariable("id") UUID id) {
                TranslationDictResponse result = service.getById(id);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/word/{wordId}")
        @Operation(summary = "Get translations by word", description = "Retrieve all translations for a specific word")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved translations for word")
        })
        public ResponseEntity<BaseResponse<List<TranslationDictResponse>>> getByWordId(
                        @Parameter(description = "Word ID") @PathVariable("wordId") UUID wordId) {
                List<TranslationDictResponse> result = service.getByWordId(wordId);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/word/{wordId}/lang/{destLang}")
        @Operation(summary = "Get translation by word and language", description = "Retrieve a translation for a specific word and language")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved translation"),
                        @ApiResponse(responseCode = "404", description = "Translation not found")
        })
        public ResponseEntity<BaseResponse<TranslationDictResponse>> getByWordIdAndLanguage(
                        @Parameter(description = "Word ID") @PathVariable("wordId") UUID wordId,
                        @Parameter(description = "Destination language code") @PathVariable("destLang") String destLang) {
                TranslationDictResponse result = service.getByWordIdAndLanguage(wordId, destLang);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @PostMapping("/filter")
        @Operation(summary = "Filter translations", description = "Filter translations with various criteria")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully filtered translations")
        })
        public ResponseEntity<PagedResponse<TranslationDictResponse>> filter(
                        @RequestBody TranslationDictFilterRequest filterRequest) {
                Page<TranslationDictResponse> result = service.filter(filterRequest);
                return ResponseEntity.ok(PagedResponse.of(result));
        }

        @PostMapping
        @Operation(summary = "Create a translation", description = "Create a new translation")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Translation created successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid request data"),
                        @ApiResponse(responseCode = "409", description = "Translation already exists")
        })
        public ResponseEntity<BaseResponse<TranslationDictResponse>> create(
                        @Valid @RequestBody TranslationDictCreateRequest request) {
                TranslationDictResponse result = service.create(request);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(BaseResponse.ok(result, "Translation created successfully"));
        }

        @PostMapping("/batch")
        @Operation(summary = "Create multiple translations", description = "Create multiple translations in batch")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Translations created successfully"),
                        @ApiResponse(responseCode = "400", description = "Invalid request data")
        })
        public ResponseEntity<BaseResponse<List<TranslationDictResponse>>> createBatch(
                        @Valid @RequestBody List<TranslationDictCreateRequest> requests) {
                List<TranslationDictResponse> result = service.createBatch(requests);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(BaseResponse.ok(result, "Translations created successfully"));
        }

        @PatchMapping("/{id}")
        @Operation(summary = "Update a translation", description = "Partially update an existing translation")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Translation updated successfully"),
                        @ApiResponse(responseCode = "404", description = "Translation not found"),
                        @ApiResponse(responseCode = "400", description = "Invalid request data")
        })
        public ResponseEntity<BaseResponse<TranslationDictResponse>> partialUpdate(
                        @Parameter(description = "Translation ID") @PathVariable("id") UUID id,
                        @RequestBody TranslationDictUpdateRequest request) {
                TranslationDictResponse result = service.partialUpdate(id, request);
                return ResponseEntity.ok(BaseResponse.ok(result, "Translation updated successfully"));
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Delete a translation", description = "Soft delete a translation")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Translation deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "Translation not found")
        })
        public ResponseEntity<BaseResponse<Void>> softDelete(
                        @Parameter(description = "Translation ID") @PathVariable("id") UUID id) {
                service.softDelete(id);
                return ResponseEntity.ok(BaseResponse.ok("Translation deleted successfully"));
        }
}
