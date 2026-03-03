package com.e4u.learning_service.controllers;

import com.e4u.learning_service.common.utils.SecurityUtil;
import com.e4u.learning_service.dtos.request.UserExerciseAttemptRequest;
import com.e4u.learning_service.dtos.request.UserLessonSessionStartRequest;
import com.e4u.learning_service.dtos.response.BaseResponse;
import com.e4u.learning_service.dtos.response.UserExerciseAttemptResponse;
import com.e4u.learning_service.dtos.response.UserLessonSessionDetailResponse;
import com.e4u.learning_service.dtos.response.UserLessonSessionResponse;
import com.e4u.learning_service.entities.UserLessonSession.SessionStatus;
import com.e4u.learning_service.services.UserExerciseAttemptService;
import com.e4u.learning_service.services.UserLessonSessionService;
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
 * REST Controller for user lesson sessions and exercise attempts.
 * Manages user's learning execution state.
 */
@RestController
@RequestMapping("/v1/lesson-sessions")
@RequiredArgsConstructor
@Tag(name = "Lesson Sessions", description = "APIs for managing user lesson sessions and exercise attempts")
public class LessonSessionController {

        private final UserLessonSessionService sessionService;
        private final UserExerciseAttemptService attemptService;

        // ==================== Session Operations ====================

        @PostMapping("/start")
        @Operation(summary = "Start or resume lesson session", description = "Start a new session or resume an existing one")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Session started/resumed successfully"),
                        @ApiResponse(responseCode = "404", description = "Lesson template not found")
        })
        public ResponseEntity<BaseResponse<UserLessonSessionDetailResponse>> startOrResumeSession(
                        @Valid @RequestBody UserLessonSessionStartRequest request) {
                UserLessonSessionDetailResponse result = sessionService.startOrResumeSession(request);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/{sessionId}")
        @Operation(summary = "Get session by ID", description = "Retrieve session summary by ID")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Session retrieved successfully"),
                        @ApiResponse(responseCode = "404", description = "Session not found")
        })
        public ResponseEntity<BaseResponse<UserLessonSessionResponse>> getSessionById(
                        @Parameter(description = "Session ID") @PathVariable UUID sessionId) {
                UserLessonSessionResponse result = sessionService.getSessionById(sessionId);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/{sessionId}/detail")
        @Operation(summary = "Get session detail", description = "Retrieve session with exercises and attempts")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Session detail retrieved successfully"),
                        @ApiResponse(responseCode = "404", description = "Session not found")
        })
        public ResponseEntity<BaseResponse<UserLessonSessionDetailResponse>> getSessionDetail(
                        @Parameter(description = "Session ID") @PathVariable UUID sessionId) {
                UserLessonSessionDetailResponse result = sessionService.getSessionDetail(sessionId);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/user/{userId}")
        @Operation(summary = "Get sessions by user", description = "Retrieve all sessions for a user")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Sessions retrieved successfully")
        })
        public ResponseEntity<BaseResponse<List<UserLessonSessionResponse>>> getSessionsByUser(
                        @Parameter(description = "User ID") @PathVariable UUID userId) {
                List<UserLessonSessionResponse> result = sessionService.getSessionsByUser(userId);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/user/{userId}/status/{status}")
        @Operation(summary = "Get sessions by status", description = "Retrieve sessions by user and status")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Sessions retrieved successfully")
        })
        public ResponseEntity<BaseResponse<List<UserLessonSessionResponse>>> getSessionsByUserAndStatus(
                        @Parameter(description = "User ID") @PathVariable UUID userId,
                        @Parameter(description = "Session status") @PathVariable SessionStatus status) {
                List<UserLessonSessionResponse> result = sessionService.getSessionsByUserAndStatus(userId, status);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @PostMapping("/{sessionId}/pause")
        @Operation(summary = "Pause session", description = "Pause an in-progress session")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Session paused successfully"),
                        @ApiResponse(responseCode = "404", description = "Session not found")
        })
        public ResponseEntity<BaseResponse<UserLessonSessionResponse>> pauseSession(
                        @Parameter(description = "Session ID") @PathVariable UUID sessionId) {
                UserLessonSessionResponse result = sessionService.pauseSession(sessionId);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @PostMapping("/{sessionId}/complete")
        @Operation(summary = "Complete session", description = "Mark a session as completed")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Session completed successfully"),
                        @ApiResponse(responseCode = "404", description = "Session not found")
        })
        public ResponseEntity<BaseResponse<UserLessonSessionResponse>> completeSession(
                        @Parameter(description = "Session ID") @PathVariable UUID sessionId) {
                UserLessonSessionResponse result = sessionService.completeSession(sessionId);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        // ==================== Exercise Attempt Operations ====================

        @PostMapping("/attempts")
        @Operation(summary = "Submit exercise attempt", description = "Submit an answer for an exercise")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Attempt submitted successfully"),
                        @ApiResponse(responseCode = "404", description = "Session or exercise not found")
        })
        public ResponseEntity<BaseResponse<UserExerciseAttemptResponse>> submitAttempt(
                        @Valid @RequestBody UserExerciseAttemptRequest request) {
                UserExerciseAttemptResponse result = attemptService.submitAttempt(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.ok(result));
        }

        @GetMapping("/attempts/{attemptId}")
        @Operation(summary = "Get attempt by ID", description = "Retrieve an exercise attempt by ID")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Attempt retrieved successfully"),
                        @ApiResponse(responseCode = "404", description = "Attempt not found")
        })
        public ResponseEntity<BaseResponse<UserExerciseAttemptResponse>> getAttemptById(
                        @Parameter(description = "Attempt ID") @PathVariable UUID attemptId) {
                UserExerciseAttemptResponse result = attemptService.getAttemptById(attemptId);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/{sessionId}/attempts")
        @Operation(summary = "Get attempts for session", description = "Retrieve all attempts for a session")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Attempts retrieved successfully")
        })
        public ResponseEntity<BaseResponse<List<UserExerciseAttemptResponse>>> getAttemptsBySession(
                        @Parameter(description = "Session ID") @PathVariable UUID sessionId) {
                List<UserExerciseAttemptResponse> result = attemptService.getAttemptsBySession(sessionId);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/{sessionId}/accuracy")
        @Operation(summary = "Get session accuracy", description = "Calculate accuracy rate for a session")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Accuracy calculated successfully")
        })
        public ResponseEntity<BaseResponse<Float>> getSessionAccuracy(
                        @Parameter(description = "Session ID") @PathVariable UUID sessionId) {
                float accuracy = attemptService.calculateSessionAccuracy(sessionId);
                return ResponseEntity.ok(BaseResponse.ok(accuracy));
        }

        @DeleteMapping("/{sessionId}")
        @Operation(summary = "Delete session", description = "Delete a session (admin only)")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Session deleted successfully"),
                        @ApiResponse(responseCode = "404", description = "Session not found")
        })
        public ResponseEntity<Void> deleteSession(
                        @Parameter(description = "Session ID") @PathVariable UUID sessionId) {
                sessionService.deleteSession(sessionId);
                return ResponseEntity.noContent().build();
        }

}
