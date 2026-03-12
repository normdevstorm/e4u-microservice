package com.e4u.learning_service.controllers;

import com.e4u.learning_service.dtos.request.BaselineEvaluateRequest;
import com.e4u.learning_service.dtos.response.BaseResponse;
import com.e4u.learning_service.dtos.response.BaselineEvaluateResponse;
import com.e4u.learning_service.dtos.response.BaselineQuestionResponse;
import com.e4u.learning_service.services.BaselineAssessmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Manages the F-02 English proficiency placement test.
 * Base path: /v1/baseline
 *
 * <p>
 * Flow:
 * <ol>
 * <li>FE calls GET /questions to load MCQ questions (no correct answer
 * exposed).</li>
 * <li>User answers them; FE calls POST /evaluate with the answer map.</li>
 * <li>Server returns cefrLevel + weighted score.</li>
 * <li>FE then calls PATCH /v1/profile/baseline to persist the result.</li>
 * </ol>
 */
@RestController
@RequestMapping("/v1/baseline")
@RequiredArgsConstructor
@Tag(name = "Baseline Assessment", description = "F-02 English proficiency placement test")
public class BaselineAssessmentController {

    private final BaselineAssessmentService baselineService;

    @GetMapping("/questions")
    @Operation(summary = "Fetch placement-test questions", description = "Returns all MCQ questions ordered by CEFR tier (A1→C1) then sort_order. "
            +
            "Correct answers are NOT included in the response.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Questions fetched successfully")
    })
    public ResponseEntity<BaseResponse<List<BaselineQuestionResponse>>> getQuestions() {
        return ResponseEntity.ok(
                BaseResponse.ok(baselineService.getQuestions(), "Questions fetched"));
    }

    @PostMapping("/evaluate")
    @Operation(summary = "Evaluate placement-test answers", description = "Accepts a map of questionId → selected option. "
            +
            "Returns the derived CEFR level and weighted percentage score.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evaluation completed"),
            @ApiResponse(responseCode = "400", description = "answers map is null or empty")
    })
    public ResponseEntity<BaseResponse<BaselineEvaluateResponse>> evaluate(
            @Valid @RequestBody BaselineEvaluateRequest request) {
        return ResponseEntity.ok(
                BaseResponse.ok(baselineService.evaluate(request), "Evaluation completed"));
    }
}
