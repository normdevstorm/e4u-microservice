package com.e4u.learning_service.controllers;

import com.e4u.learning_service.common.utils.SecurityUtil;
import com.e4u.learning_service.dtos.request.LessonTemplateCreateRequest;
import com.e4u.learning_service.dtos.request.LessonTemplateUpdateRequest;
import com.e4u.learning_service.dtos.response.BaseResponse;
import com.e4u.learning_service.dtos.response.LessonTemplateDetailResponse;
import com.e4u.learning_service.dtos.response.LessonTemplateResponse;
import com.e4u.learning_service.dtos.response.LessonTemplateWithStatusResponse;
import com.e4u.learning_service.services.LessonTemplateService;
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
 * REST Controller for LessonTemplate (Blueprint) operations.
 * Manages static lesson definitions within curriculum units.
 */
@RestController
@RequestMapping("/v1/lesson-templates")
@RequiredArgsConstructor
@Tag(name = "Lesson Templates", description = "APIs for managing lesson templates (blueprint layer)")
public class LessonTemplateController {

        private final LessonTemplateService lessonTemplateService;

        // ==================== GET Operations ====================

        @GetMapping("/{id}")
        @Operation(summary = "Get lesson template by ID", description = "Retrieve a specific lesson template by its ID")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved lesson template"),
                        @ApiResponse(responseCode = "404", description = "Lesson template not found")
        })
        public ResponseEntity<BaseResponse<LessonTemplateResponse>> getById(
                        @Parameter(description = "Lesson template ID") @PathVariable("id") UUID id) {
                LessonTemplateResponse result = lessonTemplateService.getLessonTemplateById(id);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/{id}/detail")
        @Operation(summary = "Get lesson template with exercises", description = "Retrieve a lesson template with its exercise templates")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved lesson template detail"),
                        @ApiResponse(responseCode = "404", description = "Lesson template not found")
        })
        public ResponseEntity<BaseResponse<LessonTemplateDetailResponse>> getDetailById(
                        @Parameter(description = "Lesson template ID") @PathVariable("id") UUID id) {
                LessonTemplateDetailResponse result = lessonTemplateService.getLessonTemplateDetail(id);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/unit/{unitId}")
        @Operation(summary = "Get lesson templates by unit", description = "Retrieve all lesson templates for a curriculum unit")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved lesson templates")
        })
        public ResponseEntity<BaseResponse<List<LessonTemplateResponse>>> getByUnit(
                        @Parameter(description = "Curriculum unit ID") @PathVariable("unitId") UUID unitId) {
                List<LessonTemplateResponse> result = lessonTemplateService.getLessonTemplatesByUnit(unitId);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @GetMapping("/unit/{unitId}/with-status")
        @Operation(summary = "Get lesson templates with user status", description = "Retrieve lesson templates combined with user's learning progress status derived from their session data")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully retrieved lesson templates with status")
        })
        public ResponseEntity<BaseResponse<List<LessonTemplateWithStatusResponse>>> getByUnitWithUserStatus(
                        @Parameter(description = "Curriculum unit ID") @PathVariable("unitId") UUID unitId,
                        @Parameter(description = "User ID") @RequestParam(name = "userId", required = false) UUID userId) {
                if (userId == null) {
                        userId = SecurityUtil.getCurrentUserId();
                }

                List<LessonTemplateWithStatusResponse> result = lessonTemplateService
                                .getLessonTemplatesByUnitWithUserStatus(unitId, userId);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        // ==================== POST Operations ====================

        @PostMapping
        @Operation(summary = "Create lesson template", description = "Create a new lesson template")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Successfully created lesson template"),
                        @ApiResponse(responseCode = "400", description = "Invalid request data")
        })
        public ResponseEntity<BaseResponse<LessonTemplateResponse>> create(
                        @Valid @RequestBody LessonTemplateCreateRequest request) {
                LessonTemplateResponse result = lessonTemplateService.createLessonTemplate(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.ok(result));
        }

        // ==================== PUT Operations ====================

        @PutMapping("/{id}")
        @Operation(summary = "Update lesson template", description = "Update an existing lesson template")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully updated lesson template"),
                        @ApiResponse(responseCode = "404", description = "Lesson template not found")
        })
        public ResponseEntity<BaseResponse<LessonTemplateResponse>> update(
                        @Parameter(description = "Lesson template ID") @PathVariable("id") UUID id,
                        @Valid @RequestBody LessonTemplateUpdateRequest request) {
                LessonTemplateResponse result = lessonTemplateService.updateLessonTemplate(id, request);
                return ResponseEntity.ok(BaseResponse.ok(result));
        }

        @PutMapping("/unit/{unitId}/reorder")
        @Operation(summary = "Reorder lessons", description = "Reorder lessons within a unit")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Successfully reordered lessons")
        })
        public ResponseEntity<BaseResponse<Void>> reorderLessons(
                        @Parameter(description = "Curriculum unit ID") @PathVariable("unitId") UUID unitId,
                        @RequestBody List<UUID> lessonIds) {
                lessonTemplateService.reorderLessons(unitId, lessonIds);
                return ResponseEntity.ok(BaseResponse.ok(null));
        }

        // ==================== DELETE Operations ====================

        @DeleteMapping("/{id}")
        @Operation(summary = "Delete lesson template", description = "Delete a lesson template")
        @ApiResponses({
                        @ApiResponse(responseCode = "204", description = "Successfully deleted lesson template"),
                        @ApiResponse(responseCode = "404", description = "Lesson template not found")
        })
        public ResponseEntity<Void> delete(
                        @Parameter(description = "Lesson template ID") @PathVariable("id") UUID id) {
                lessonTemplateService.deleteLessonTemplate(id);
                return ResponseEntity.noContent().build();
        }
}
