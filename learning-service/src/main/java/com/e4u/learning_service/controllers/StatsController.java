package com.e4u.learning_service.controllers;

import com.e4u.learning_service.common.utils.SecurityUtil;
import com.e4u.learning_service.dtos.response.BaseResponse;
import com.e4u.learning_service.dtos.response.LearningActivityResponse;
import com.e4u.learning_service.dtos.response.PagedResponse;
import com.e4u.learning_service.dtos.response.SessionHistoryResponse;
import com.e4u.learning_service.dtos.response.UserStatsResponse;
import com.e4u.learning_service.dtos.response.VocabularyStatsResponse;
import com.e4u.learning_service.services.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller exposing user learning statistics endpoints.
 * All endpoints derive the user identity from the JWT — no userId path/query
 * param is accepted.
 */
@RestController
@RequestMapping("/v1/stats")
@RequiredArgsConstructor
@Tag(name = "Statistics", description = "User learning statistics and activity data")
public class StatsController {

        private final StatsService statsService;

        /**
         * GET /v1/stats/overview
         * Returns aggregated KPIs: streak, total words, study time, accuracy, last
         * study date.
         */
        @GetMapping("/overview")
        @Operation(summary = "Get user stats overview", description = "Returns streak, total words learned, study time, overall accuracy and last study date")
        public ResponseEntity<BaseResponse<UserStatsResponse>> getUserStats() {
                UUID userId = SecurityUtil.getCurrentUserId();
                UserStatsResponse stats = statsService.getUserStats(userId);
                return ResponseEntity.ok(BaseResponse.ok(stats));
        }

        /**
         * GET /v1/stats/weekly
         * Returns exactly 7 daily activity records (oldest → newest) for bar-chart
         * rendering.
         */
        @GetMapping("/weekly")
        @Operation(summary = "Get weekly learning activity", description = "Returns 7 days of activity: sessions completed, words learned, accuracy per day")
        public ResponseEntity<BaseResponse<List<LearningActivityResponse>>> getWeeklyActivity() {
                UUID userId = SecurityUtil.getCurrentUserId();
                List<LearningActivityResponse> activity = statsService.getWeeklyActivity(userId);
                return ResponseEntity.ok(BaseResponse.ok(activity));
        }

        /**
         * GET /v1/stats/vocabulary
         * Returns vocabulary breakdown for a donut/pie chart.
         */
        @GetMapping("/vocabulary")
        @Operation(summary = "Get vocabulary statistics", description = "Returns vocabulary distribution: mastered / learning / needs-review / new words")
        public ResponseEntity<BaseResponse<VocabularyStatsResponse>> getVocabularyStats() {
                UUID userId = SecurityUtil.getCurrentUserId();
                VocabularyStatsResponse vocabStats = statsService.getVocabularyStats(userId);
                return ResponseEntity.ok(BaseResponse.ok(vocabStats));
        }

        /**
         * GET /v1/stats/sessions?page=0&size=10
         * Returns a paginated list of completed sessions, most-recent first.
         */
        @GetMapping("/sessions")
        @Operation(summary = "Get session history", description = "Returns paginated list of completed learning sessions ordered by most recent first")
        public ResponseEntity<PagedResponse<SessionHistoryResponse>> getSessionHistory(
                        @Parameter(description = "0-indexed page number") @RequestParam(name = "page", defaultValue = "0") int page,
                        @Parameter(description = "Number of items per page") @RequestParam(name = "size", defaultValue = "10") int size) {
                UUID userId = SecurityUtil.getCurrentUserId();
                Page<SessionHistoryResponse> resultPage = statsService.getSessionHistory(
                                userId,
                                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

                return ResponseEntity.ok(PagedResponse.of(
                                resultPage.getContent(),
                                page,
                                size,
                                resultPage.getTotalElements()));
        }
}
