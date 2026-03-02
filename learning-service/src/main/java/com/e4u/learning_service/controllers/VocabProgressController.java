package com.e4u.learning_service.controllers;

import com.e4u.learning_service.dtos.request.UserVocabProgressCreateRequest;
import com.e4u.learning_service.dtos.response.BaseResponse;
import com.e4u.learning_service.dtos.response.UserVocabProgressResponse;
import com.e4u.learning_service.services.UserVocabProgressService;
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
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for user vocabulary progress (SRS-based learning).
 * Manages user's vocabulary mastery and spaced repetition scheduling.
 */
@RestController
@RequestMapping("/v1/vocab-progress")
@RequiredArgsConstructor
@Tag(name = "Vocabulary Progress", description = "APIs for managing user vocabulary progress with SRS")
public class VocabProgressController {

    private final UserVocabProgressService progressService;

    // ==================== GET Operations ====================

    @GetMapping("/{id}")
    @Operation(summary = "Get progress by ID", description = "Retrieve vocab progress by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Progress retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Progress not found")
    })
    public ResponseEntity<BaseResponse<UserVocabProgressResponse>> getById(
            @Parameter(description = "Progress ID") @PathVariable UUID id) {
        UserVocabProgressResponse result = progressService.getProgressById(id);
        return ResponseEntity.ok(BaseResponse.ok(result));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all progress for user", description = "Retrieve all vocab progress for a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Progress list retrieved successfully")
    })
    public ResponseEntity<BaseResponse<List<UserVocabProgressResponse>>> getByUser(
            @Parameter(description = "User ID") @PathVariable UUID userId) {
        List<UserVocabProgressResponse> result = progressService.getProgressByUser(userId);
        return ResponseEntity.ok(BaseResponse.ok(result));
    }

    @GetMapping("/user/{userId}/word/{wordId}")
    @Operation(summary = "Get progress for specific word", description = "Retrieve progress for a user-word pair")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Progress retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Progress not found")
    })
    public ResponseEntity<BaseResponse<UserVocabProgressResponse>> getByUserAndWord(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @Parameter(description = "Word ID") @PathVariable UUID wordId) {
        UserVocabProgressResponse result = progressService.getProgressByUserAndWord(userId, wordId);
        return ResponseEntity.ok(BaseResponse.ok(result));
    }

    @GetMapping("/user/{userId}/due-for-review")
    @Operation(summary = "Get words due for review", description = "Retrieve words due for SRS review")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Due words retrieved successfully")
    })
    public ResponseEntity<BaseResponse<List<UserVocabProgressResponse>>> getDueForReview(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @Parameter(description = "Limit number of words") @RequestParam(required = false) Integer limit) {
        List<UserVocabProgressResponse> result;
        if (limit != null && limit > 0) {
            result = progressService.getWordsDueForReview(userId, limit);
        } else {
            result = progressService.getWordsDueForReview(userId);
        }
        return ResponseEntity.ok(BaseResponse.ok(result));
    }

    @GetMapping("/user/{userId}/mastered")
    @Operation(summary = "Get mastered words", description = "Retrieve all mastered words for a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Mastered words retrieved successfully")
    })
    public ResponseEntity<BaseResponse<List<UserVocabProgressResponse>>> getMasteredWords(
            @Parameter(description = "User ID") @PathVariable UUID userId) {
        List<UserVocabProgressResponse> result = progressService.getMasteredWords(userId);
        return ResponseEntity.ok(BaseResponse.ok(result));
    }

    @GetMapping("/user/{userId}/learning")
    @Operation(summary = "Get words in learning", description = "Retrieve all words still being learned")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Learning words retrieved successfully")
    })
    public ResponseEntity<BaseResponse<List<UserVocabProgressResponse>>> getWordsInLearning(
            @Parameter(description = "User ID") @PathVariable UUID userId) {
        List<UserVocabProgressResponse> result = progressService.getWordsInLearning(userId);
        return ResponseEntity.ok(BaseResponse.ok(result));
    }

    @GetMapping("/user/{userId}/stats")
    @Operation(summary = "Get vocabulary statistics", description = "Get mastered and learning word counts")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stats retrieved successfully")
    })
    public ResponseEntity<BaseResponse<Map<String, Long>>> getVocabStats(
            @Parameter(description = "User ID") @PathVariable UUID userId) {
        long mastered = progressService.countMasteredWords(userId);
        long learning = progressService.countWordsInLearning(userId);
        Map<String, Long> stats = Map.of(
                "mastered", mastered,
                "learning", learning,
                "total", mastered + learning
        );
        return ResponseEntity.ok(BaseResponse.ok(stats));
    }

    // ==================== POST Operations ====================

    @PostMapping
    @Operation(summary = "Create or get progress", description = "Create progress for a word or get existing")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Progress created/retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Word not found")
    })
    public ResponseEntity<BaseResponse<UserVocabProgressResponse>> createOrGetProgress(
            @Valid @RequestBody UserVocabProgressCreateRequest request) {
        UserVocabProgressResponse result = progressService.createOrGetProgress(request);
        return ResponseEntity.ok(BaseResponse.ok(result));
    }

    @PostMapping("/user/{userId}/initialize")
    @Operation(summary = "Initialize progress for words", description = "Batch initialize progress for multiple words")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Progress initialized successfully")
    })
    public ResponseEntity<BaseResponse<List<UserVocabProgressResponse>>> initializeProgress(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @RequestBody List<UUID> wordIds) {
        List<UserVocabProgressResponse> result = progressService.initializeProgressForWords(userId, wordIds);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.ok(result));
    }

    // ==================== PUT Operations ====================

    @PutMapping("/user/{userId}/word/{wordId}/correct")
    @Operation(summary = "Record correct answer", description = "Record a correct answer and update SRS parameters")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Answer recorded successfully")
    })
    public ResponseEntity<BaseResponse<UserVocabProgressResponse>> recordCorrectAnswer(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @Parameter(description = "Word ID") @PathVariable UUID wordId) {
        UserVocabProgressResponse result = progressService.recordCorrectAnswer(userId, wordId);
        return ResponseEntity.ok(BaseResponse.ok(result));
    }

    @PutMapping("/user/{userId}/word/{wordId}/incorrect")
    @Operation(summary = "Record incorrect answer", description = "Record an incorrect answer and update SRS parameters")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Answer recorded successfully")
    })
    public ResponseEntity<BaseResponse<UserVocabProgressResponse>> recordIncorrectAnswer(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @Parameter(description = "Word ID") @PathVariable UUID wordId) {
        UserVocabProgressResponse result = progressService.recordIncorrectAnswer(userId, wordId);
        return ResponseEntity.ok(BaseResponse.ok(result));
    }

    @PutMapping("/user/{userId}/word/{wordId}/relevance")
    @Operation(summary = "Update relevance score", description = "Update the relevance score for a word")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Relevance updated successfully")
    })
    public ResponseEntity<BaseResponse<UserVocabProgressResponse>> updateRelevanceScore(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @Parameter(description = "Word ID") @PathVariable UUID wordId,
            @Parameter(description = "New relevance score (0.0-1.0)") @RequestParam float score) {
        UserVocabProgressResponse result = progressService.updateRelevanceScore(userId, wordId, score);
        return ResponseEntity.ok(BaseResponse.ok(result));
    }

    @PutMapping("/user/{userId}/word/{wordId}/context")
    @Operation(summary = "Update active context", description = "Update the active context for a word")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Context updated successfully"),
            @ApiResponse(responseCode = "404", description = "Progress or context not found")
    })
    public ResponseEntity<BaseResponse<UserVocabProgressResponse>> updateActiveContext(
            @Parameter(description = "User ID") @PathVariable UUID userId,
            @Parameter(description = "Word ID") @PathVariable UUID wordId,
            @Parameter(description = "Context ID") @RequestParam UUID contextId) {
        UserVocabProgressResponse result = progressService.updateActiveContext(userId, wordId, contextId);
        return ResponseEntity.ok(BaseResponse.ok(result));
    }

    // ==================== DELETE Operations ====================

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete progress", description = "Delete a vocab progress record")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Progress deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Progress not found")
    })
    public ResponseEntity<Void> deleteProgress(
            @Parameter(description = "Progress ID") @PathVariable UUID id) {
        progressService.deleteProgress(id);
        return ResponseEntity.noContent().build();
    }
}
