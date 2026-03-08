package com.e4u.ai_filter_service.controller;

import com.e4u.ai_filter_service.domain.entity.FilterJobItem;
import com.e4u.ai_filter_service.service.FilterJobManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST API for managing AI filter batch job executions.
 *
 * <p>
 * Endpoints:
 * <ul>
 *   <li>POST  /api/filter-jobs/trigger — manual job trigger</li>
 *   <li>GET   /api/filter-jobs — list running job executions</li>
 *   <li>GET   /api/filter-jobs/{executionId} — specific job execution detail</li>
 *   <li>POST  /api/filter-jobs/{executionId}/stop — stop a running job</li>
 *   <li>GET   /api/filter-jobs/{executionId}/items — items for a job execution</li>
 *   <li>GET   /api/filter-jobs/by-user/{userId} — all results for a user</li>
 *   <li>GET   /api/filter-jobs/by-user/{userId}/high — HIGH-tier words for user</li>
 *   <li>GET   /api/filter-jobs/by-user/{userId}/low — LOW-tier words for user</li>
 *   <li>GET   /api/filter-jobs/words/{wordId}/history — AI history for a word</li>
 *   <li>GET   /api/filter-jobs/pending-count — total pending template queue depth</li>
 *   <li>GET   /api/filter-jobs/by-user/{userId}/pending-count — per-user queue depth</li>
 * </ul>
 */
@Slf4j
@RestController
@RequestMapping("/api/filter-jobs")
@RequiredArgsConstructor
@Tag(name = "Filter Jobs", description = "AI word relevance batch job management")
public class FilterJobController {

    private final FilterJobManagementService filterJobManagementService;

    // ─── Trigger ──────────────────────────────────────────────────────────────

    @Operation(summary = "Manually trigger a new AI filter job run")
    @PostMapping("/trigger")
    public ResponseEntity<String> triggerJob() {
        log.info("POST /api/filter-jobs/trigger called");
        // TODO: return job execution ID once async launch is wired
        filterJobManagementService.triggerManual();
        return ResponseEntity.accepted().body("Job triggered successfully. Check /api/filter-jobs for status.");
    }

    // ─── Execution status ─────────────────────────────────────────────────────

    @Operation(summary = "List all running job executions")
    @GetMapping
    public ResponseEntity<List<JobExecution>> listJobs() {
        // TODO: map to a proper DTO instead of exposing JobExecution directly
        return ResponseEntity.ok(filterJobManagementService.listExecutions());
    }

    @Operation(summary = "Get details for a specific job execution")
    @GetMapping("/{executionId}")
    public ResponseEntity<JobExecution> getJob(
            @Parameter(description = "Spring Batch job execution ID") @PathVariable Long executionId) {
        // TODO: map to a proper DTO
        return ResponseEntity.ok(filterJobManagementService.getExecution(executionId));
    }

    @Operation(summary = "Stop a running job execution (graceful — completes current chunk first)")
    @PostMapping("/{executionId}/stop")
    public ResponseEntity<Void> stopJob(@PathVariable Long executionId) {
        filterJobManagementService.stopExecution(executionId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List per-word relevance results for a specific job execution")
    @GetMapping("/{executionId}/items")
    public ResponseEntity<Page<FilterJobItem>> getJobItems(
            @PathVariable Long executionId,
            @PageableDefault(size = 50) Pageable pageable) {
        // TODO: map to a DTO
        return ResponseEntity.ok(filterJobManagementService.getItems(executionId, pageable));
    }

    // ─── Per-user relevance results ───────────────────────────────────────────

    @Operation(summary = "Get all AI relevance results for a specific user (all tiers)")
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<Page<FilterJobItem>> getItemsByUser(
            @Parameter(description = "UUID of the learner") @PathVariable UUID userId,
            @PageableDefault(size = 50) Pageable pageable) {
        // TODO: map to a DTO with curriculum selection status
        return ResponseEntity.ok(filterJobManagementService.getItemsByUser(userId, pageable));
    }

    @Operation(summary = "Get HIGH-relevance words for a user (selected into curriculum)")
    @GetMapping("/by-user/{userId}/high")
    public ResponseEntity<Page<FilterJobItem>> getHighRelevanceItems(
            @Parameter(description = "UUID of the learner") @PathVariable UUID userId,
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(filterJobManagementService.getHighRelevanceItems(userId, pageable));
    }

    @Operation(summary = "Get LOW-relevance words for a user (rejected from curriculum)")
    @GetMapping("/by-user/{userId}/low")
    public ResponseEntity<Page<FilterJobItem>> getLowRelevanceItems(
            @Parameter(description = "UUID of the learner") @PathVariable UUID userId,
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(filterJobManagementService.getLowRelevanceItems(userId, pageable));
    }

    // ─── Word history ─────────────────────────────────────────────────────────

    @Operation(summary = "Get full AI filter history for a word across all users")
    @GetMapping("/words/{wordId}/history")
    public ResponseEntity<List<FilterJobItem>> getWordHistory(
            @Parameter(description = "UUID of the word in global_dictionary (e4u_learning)")
            @PathVariable UUID wordId) {
        return ResponseEntity.ok(filterJobManagementService.getWordHistory(wordId));
    }

    // ─── Queue depth ──────────────────────────────────────────────────────────

    @Operation(summary = "Count total word_context_templates pending AI evaluation (global queue depth)")
    @GetMapping("/pending-count")
    public ResponseEntity<Long> getPendingCount() {
        return ResponseEntity.ok(filterJobManagementService.countPendingTemplates());
    }

    @Operation(summary = "Count word_context_templates pending AI evaluation for a specific user")
    @GetMapping("/by-user/{userId}/pending-count")
    public ResponseEntity<Long> getPendingCountByUser(
            @Parameter(description = "UUID of the learner") @PathVariable UUID userId) {
        return ResponseEntity.ok(filterJobManagementService.countPendingTemplatesByUser(userId));
    }
}
