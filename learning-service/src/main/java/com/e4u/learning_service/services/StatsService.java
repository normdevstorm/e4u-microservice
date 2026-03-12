package com.e4u.learning_service.services;

import com.e4u.learning_service.dtos.response.LearningActivityResponse;
import com.e4u.learning_service.dtos.response.SessionHistoryResponse;
import com.e4u.learning_service.dtos.response.UserStatsResponse;
import com.e4u.learning_service.dtos.response.VocabularyStatsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for user learning statistics.
 * All methods are scoped to a specific user — userId is resolved from JWT
 * at the controller layer and passed in.
 */
public interface StatsService {

    /**
     * Overview KPIs: streak, total words learned, study time, accuracy, last study
     * date.
     *
     * @param userId authenticated user's UUID
     * @return aggregated stats overview
     */
    UserStatsResponse getUserStats(UUID userId);

    /**
     * 7-day activity breakdown suitable for a bar chart.
     * Always returns exactly 7 entries (one per day), with zeros for days with no
     * activity.
     *
     * @param userId authenticated user's UUID
     * @return list of 7 daily activity records, oldest → newest
     */
    List<LearningActivityResponse> getWeeklyActivity(UUID userId);

    /**
     * Vocabulary distribution: mastered / learning / needs-review / new.
     *
     * @param userId authenticated user's UUID
     * @return vocabulary breakdown counts
     */
    VocabularyStatsResponse getVocabularyStats(UUID userId);

    /**
     * Paginated completed session history, most-recent first.
     *
     * @param userId   authenticated user's UUID
     * @param pageable page and size parameters
     * @return page of session history entries
     */
    Page<SessionHistoryResponse> getSessionHistory(UUID userId, Pageable pageable);
}
