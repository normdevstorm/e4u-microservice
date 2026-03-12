package com.e4u.learning_service.services.impl;

import com.e4u.learning_service.dtos.response.LearningActivityResponse;
import com.e4u.learning_service.dtos.response.SessionHistoryResponse;
import com.e4u.learning_service.dtos.response.UserStatsResponse;
import com.e4u.learning_service.dtos.response.VocabularyStatsResponse;
import com.e4u.learning_service.entities.UserLessonSession;
import com.e4u.learning_service.repositories.UserLessonSessionRepository;
import com.e4u.learning_service.repositories.UserVocabProgressRepository;
import com.e4u.learning_service.services.StatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of StatsService.
 * Computes all statistics live from the source-of-truth tables
 * (user_lesson_sessions, user_vocab_progress, user_exercise_attempts).
 */
@Service
@RequiredArgsConstructor
@Log4j2
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_INSTANT;

    private final UserLessonSessionRepository sessionRepository;
    private final UserVocabProgressRepository vocabProgressRepository;

    // ── Overview ──────────────────────────────────────────────────────────────

    @Override
    public UserStatsResponse getUserStats(UUID userId) {
        // 1. Streak calculation from distinct study dates (descending)
        List<Date> studyDates = sessionRepository.findDistinctStudyDates(userId);
        int[] streaks = calculateStreaks(studyDates);

        // 2. Total mastered words
        long totalWordsLearned = vocabProgressRepository.countByUserIdAndIsMasteredTrue(userId);

        // 3. Total study time in minutes (from exercise attempt durations)
        Long totalStudyTimeSeconds = sessionRepository.sumTotalStudyTimeSecondsByUserId(userId);
        long totalStudyTimeMinutes = (totalStudyTimeSeconds != null ? totalStudyTimeSeconds : 0L) / 60;

        // 4. Overall accuracy — average of all completed session accuracyRates,
        // normalised 0.0–1.0
        Optional<Float> avgAccuracy = sessionRepository.findAverageAccuracyByUserId(userId);
        double overallAccuracy = avgAccuracy.map(a -> (double) a / 100.0).orElse(0.0);

        // 5. Last study date
        String lastStudyDate = studyDates.isEmpty()
                ? null
                : studyDates.get(0).toLocalDate().format(DATE_FORMATTER);

        log.debug("getUserStats for user {}: streak={}/{}, words={}, time={}min, accuracy={}",
                userId, streaks[0], streaks[1], totalWordsLearned, totalStudyTimeMinutes, overallAccuracy);

        return UserStatsResponse.builder()
                .currentStreak(streaks[0])
                .longestStreak(streaks[1])
                .totalWordsLearned((int) totalWordsLearned)
                .totalStudyTimeMinutes(totalStudyTimeMinutes)
                .overallAccuracy(overallAccuracy)
                .lastStudyDate(lastStudyDate)
                .build();
    }

    /**
     * Calculates [currentStreak, longestStreak] from a descending list of distinct
     * study dates.
     *
     * Algorithm:
     * - Walk through dates from most recent to oldest.
     * - currentStreak: count consecutive days starting from today or yesterday
     * (chain breaks if gap > 1 day).
     * - longestStreak: widest consecutive window ever found in the full history.
     */
    private int[] calculateStreaks(List<Date> studyDates) {
        if (studyDates.isEmpty())
            return new int[] { 0, 0 };

        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        List<LocalDate> dates = studyDates.stream()
                .map(Date::toLocalDate)
                .collect(Collectors.toList()); // already sorted DESC by query

        // ── current streak ────────────────────────────────────────────────────
        int current = 0;
        LocalDate expected = today;

        for (LocalDate date : dates) {
            // Allow streak to start on today OR yesterday (user hasn't studied yet today)
            if (current == 0 && (date.isEqual(today) || date.isEqual(today.minusDays(1)))) {
                expected = date;
            }
            if (date.isEqual(expected)) {
                current++;
                expected = expected.minusDays(1);
            } else if (date.isBefore(expected)) {
                // Gap detected — streak chain broken
                break;
            }
        }

        // ── longest streak ────────────────────────────────────────────────────
        int longest = 0;
        int run = 0;
        LocalDate prev = null;

        // Walk dates from OLDEST to NEWEST for longest streak calculation
        List<LocalDate> ascending = new ArrayList<>(dates);
        Collections.reverse(ascending);

        for (LocalDate date : ascending) {
            if (prev == null || date.isEqual(prev.plusDays(1))) {
                run++;
            } else {
                run = 1; // reset on gap
            }
            if (run > longest)
                longest = run;
            prev = date;
        }

        return new int[] { current, longest };
    }

    // ── Weekly Activity ────────────────────────────────────────────────────────

    @Override
    public List<LearningActivityResponse> getWeeklyActivity(UUID userId) {
        // Build a 7-day window: start of 6 days ago → end of today (exclusive tomorrow)
        Instant to = LocalDate.now(ZoneOffset.UTC).plusDays(1)
                .atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant from = LocalDate.now(ZoneOffset.UTC).minusDays(6)
                .atStartOfDay(ZoneOffset.UTC).toInstant();

        List<Object[]> rawRows = sessionRepository.findWeeklyActivityRaw(userId, from, to);

        // Index raw rows by date string for O(1) lookup
        Map<String, Object[]> activityByDate = new LinkedHashMap<>();
        for (Object[] row : rawRows) {
            // row: [activity_date, sessions_count, words_learned, total_items_sum,
            // avg_accuracy, study_time_seconds]
            // words_learned (index 2) = COUNT(DISTINCT user_vocab_progress.id) created on
            // this day
            String dateStr = row[0].toString(); // Date.toString() returns "yyyy-MM-dd"
            activityByDate.put(dateStr, row);
        }

        // Build 7-entry list with zeros for days with no activity
        List<LearningActivityResponse> result = new ArrayList<>(7);
        for (int i = 6; i >= 0; i--) {
            LocalDate day = LocalDate.now(ZoneOffset.UTC).minusDays(i);
            String dateStr = day.format(DATE_FORMATTER);
            Object[] row = activityByDate.get(dateStr);

            if (row != null) {
                long sessionsCount = toLong(row[1]);
                long wordsLearned = toLong(row[2]); // distinct vocab words added to SRS on this day
                double avgAccuracy = toDouble(row[4]) / 100.0; // normalise percentage → 0.0–1.0
                int studyTimeMinutes = (int) (toLong(row[5]) / 60);

                result.add(LearningActivityResponse.builder()
                        .date(dateStr)
                        .wordsLearned((int) wordsLearned)
                        .studyTimeMinutes(studyTimeMinutes)
                        .sessionsCompleted((int) sessionsCount)
                        .accuracy(avgAccuracy)
                        .build());
            } else {
                result.add(LearningActivityResponse.builder()
                        .date(dateStr)
                        .wordsLearned(0)
                        .studyTimeMinutes(0)
                        .sessionsCompleted(0)
                        .accuracy(0.0)
                        .build());
            }
        }

        return result;
    }

    // ── Vocabulary Stats ───────────────────────────────────────────────────────

    @Override
    public VocabularyStatsResponse getVocabularyStats(UUID userId) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        long mastered = vocabProgressRepository.countByUserIdAndIsMasteredTrue(userId);
        long learning = vocabProgressRepository.countLearningWords(userId, now);
        long needsReview = vocabProgressRepository.countNeedsReviewWords(userId, now);
        // newWords: intervalDays == 0 (never scheduled by SRS)
        long newWords = vocabProgressRepository.countByUserIdAndIntervalDaysEquals(userId, 0);

        log.debug("getVocabularyStats for user {}: mastered={}, learning={}, review={}, new={}",
                userId, mastered, learning, needsReview, newWords);

        return VocabularyStatsResponse.builder()
                .mastered(mastered)
                .learning(learning)
                .needsReview(needsReview)
                .newWords(newWords)
                .build();
    }

    // ── Session History ────────────────────────────────────────────────────────

    @Override
    public Page<SessionHistoryResponse> getSessionHistory(UUID userId, Pageable pageable) {
        // Use a manual count query to avoid Hibernate count-with-fetch-join warnings
        long total = sessionRepository.countCompletedSessionsByUserId(userId);
        Page<UserLessonSession> page = sessionRepository.findCompletedSessionsByUserId(userId, pageable);

        List<SessionHistoryResponse> content = page.getContent().stream()
                .map(this::toSessionHistoryResponse)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, total);
    }

    private SessionHistoryResponse toSessionHistoryResponse(UserLessonSession session) {
        String unitTitle = (session.getLessonTemplate() != null)
                ? session.getLessonTemplate().getLessonName()
                : "Lesson";

        // Sum attempt durations for this session (may be 0 if no attempts recorded)
        int durationMinutes = session.getExerciseAttempts().stream()
                .mapToInt(a -> a.getTimeTakenSeconds() != null ? a.getTimeTakenSeconds() : 0)
                .sum() / 60;

        double accuracy = session.getAccuracyRate() != null
                ? session.getAccuracyRate() / 100.0
                : 0.0;

        return SessionHistoryResponse.builder()
                .id(session.getId().toString())
                .startTime(DATE_TIME_FORMATTER.format(session.getCreatedAt()))
                .durationMinutes(durationMinutes)
                .unitTitle(unitTitle)
                .wordsStudied(session.getTotalItems() != null ? session.getTotalItems() : 0)
                .accuracy(accuracy)
                .completedPhase("STUDY") // TODO: extend when phase tracking is implemented
                .build();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private long toLong(Object value) {
        if (value == null)
            return 0L;
        if (value instanceof Number)
            return ((Number) value).longValue();
        return Long.parseLong(value.toString());
    }

    private double toDouble(Object value) {
        if (value == null)
            return 0.0;
        if (value instanceof BigDecimal)
            return ((BigDecimal) value).doubleValue();
        if (value instanceof Number)
            return ((Number) value).doubleValue();
        return Double.parseDouble(value.toString());
    }
}
