package com.e4u.learning_service.controllers;

import com.e4u.learning_service.dtos.request.ExerciseTemplateCreateRequest;
import com.e4u.learning_service.dtos.request.ExerciseTemplateUpdateRequest;
import com.e4u.learning_service.dtos.response.BaseResponse;
import com.e4u.learning_service.dtos.response.ExerciseTemplateResponse;
import com.e4u.learning_service.entities.ExerciseTemplate.ExerciseType;
import com.e4u.learning_service.services.ExerciseTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for ExerciseTemplate (Blueprint) operations.
 * Manages exercise templates - both shared and user-specific.
 */
@RestController
@RequestMapping("/v1/exercise-templates")
@RequiredArgsConstructor
@Tag(name = "Exercise Templates", description = "APIs for managing exercise templates (blueprint layer)")
public class ExerciseTemplateController {

    private final ExerciseTemplateService exerciseTemplateService;

    // ==================== GET Operations ====================

    @GetMapping("/{id}")
    @Operation(summary = "Get exercise template by ID", description = "Retrieve a specific exercise template")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved exercise template"),
            @ApiResponse(responseCode = "404", description = "Exercise template not found")
    })
    public ResponseEntity<BaseResponse<ExerciseTemplateResponse>> getById(
            @Parameter(description = "Exercise template ID") @PathVariable UUID id) {
        ExerciseTemplateResponse result = exerciseTemplateService.getExerciseTemplateById(id);
        return ResponseEntity.ok(BaseResponse.ok(result));
    }

    @GetMapping("/lesson/{lessonTemplateId}")
    @Operation(summary = "Get exercises for lesson", description = "Retrieve all exercises for a lesson template")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved exercises")
    })
    public ResponseEntity<BaseResponse<List<ExerciseTemplateResponse>>> getByLesson(
            @Parameter(description = "Lesson template ID") @PathVariable UUID lessonTemplateId,
            @Parameter(description = "User ID (optional, for user-specific exercises)") @RequestParam(required = false) UUID userId) {
        List<ExerciseTemplateResponse> result = exerciseTemplateService.getExercisesForLesson(lessonTemplateId, userId);
        return ResponseEntity.ok(BaseResponse.ok(result));
    }

    @GetMapping("/lesson/{lessonTemplateId}/learning")
    @Operation(summary = "Get exercises for learning", description = "Retrieve exercises without exposing correct answers")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved exercises")
    })
    public ResponseEntity<BaseResponse<List<ExerciseTemplateResponse>>> getForLearning(
            @Parameter(description = "Lesson template ID") @PathVariable UUID lessonTemplateId,
            @Parameter(description = "User ID") @RequestParam UUID userId) {
        List<ExerciseTemplateResponse> result = exerciseTemplateService.getExercisesForLearning(lessonTemplateId, userId);
        return ResponseEntity.ok(BaseResponse.ok(result));
    }

    @GetMapping("/lesson/{lessonTemplateId}/type/{exerciseType}")
    @Operation(summary = "Get exercises by type", description = "Retrieve exercises of a specific type")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved exercises")
    })
    public ResponseEntity<BaseResponse<List<ExerciseTemplateResponse>>> getByType(
            @Parameter(description = "Lesson template ID") @PathVariable UUID lessonTemplateId,
            @Parameter(description = "Exercise type") @PathVariable ExerciseType exerciseType) {
        List<ExerciseTemplateResponse> result = exerciseTemplateService.getExercisesByType(lessonTemplateId, exerciseType);
        return ResponseEntity.ok(BaseResponse.ok(result));
    }

    // ==================== POST Operations ====================

    @PostMapping
    @Operation(summary = "Create exercise template", description = "Create a new exercise template")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successfully created exercise template"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<BaseResponse<ExerciseTemplateResponse>> create(
            @Valid @RequestBody ExerciseTemplateCreateRequest request) {
        ExerciseTemplateResponse result = exerciseTemplateService.createExerciseTemplate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.ok(result));
    }

    @PostMapping("/generate/word/{wordId}")
    @Operation(summary = "Generate exercise for word", description = "Auto-generate an exercise for a specific word")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successfully generated exercise"),
            @ApiResponse(responseCode = "404", description = "Word not found")
    })
    public ResponseEntity<BaseResponse<ExerciseTemplateResponse>> generateForWord(
            @Parameter(description = "Word ID") @PathVariable UUID wordId,
            @Parameter(description = "Exercise type") @RequestParam ExerciseType exerciseType,
            @Parameter(description = "User ID for user-specific exercise") @RequestParam UUID userId) {
        ExerciseTemplateResponse result = exerciseTemplateService.generateExerciseForWord(wordId, exerciseType, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.ok(result));
    }

    @PostMapping("/generate/review")
    @Operation(summary = "Generate review exercises", description = "Generate review exercises for multiple words")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successfully generated exercises")
    })
    public ResponseEntity<BaseResponse<List<ExerciseTemplateResponse>>> generateReviewExercises(
            @Parameter(description = "User ID") @RequestParam UUID userId,
            @Parameter(description = "Lesson template ID (optional)") @RequestParam(required = false) UUID lessonTemplateId,
            @RequestBody List<UUID> wordIds) {
        List<ExerciseTemplateResponse> result = exerciseTemplateService.generateReviewExercises(userId, lessonTemplateId, wordIds);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.ok(result));
    }

    // ==================== PUT Operations ====================

    @PutMapping("/{id}")
    @Operation(summary = "Update exercise template", description = "Update an existing exercise template")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully updated exercise template"),
            @ApiResponse(responseCode = "404", description = "Exercise template not found")
    })
    public ResponseEntity<BaseResponse<ExerciseTemplateResponse>> update(
            @Parameter(description = "Exercise template ID") @PathVariable UUID id,
            @Valid @RequestBody ExerciseTemplateUpdateRequest request) {
        ExerciseTemplateResponse result = exerciseTemplateService.updateExerciseTemplate(id, request);
        return ResponseEntity.ok(BaseResponse.ok(result));
    }

    // ==================== DELETE Operations ====================

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete exercise template", description = "Delete an exercise template")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Successfully deleted exercise template"),
            @ApiResponse(responseCode = "404", description = "Exercise template not found")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Exercise template ID") @PathVariable UUID id) {
        exerciseTemplateService.deleteExerciseTemplate(id);
        return ResponseEntity.noContent().build();
    }
}
