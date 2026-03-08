package com.e4u.ai_filter_service.scheduler;

import com.e4u.ai_filter_service.common.constants.FilterConstants;
import com.e4u.ai_filter_service.domain.enums.TriggerType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled entry point for the nightly AI word filtration batch job.
 *
 * <p>
 * The cron expression is driven by {@code batch.scheduler.cron} (default: 2AM
 * daily).
 * The scheduler can be completely disabled by setting
 * {@code batch.scheduler.enabled=false}
 * (useful in test or CI environments).
 *
 * <p>
 * Each invocation adds a unique {@code triggeredAt} timestamp to
 * {@code JobParameters}
 * so that Spring Batch treats it as a new {@code JobInstance} (required for
 * re-runnable jobs
 * when using {@code RunIdIncrementer}).
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "batch.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class FilterJobScheduler {

    private final JobLauncher jobLauncher;

    @Qualifier(FilterConstants.WORD_FILTER_JOB)
    private final Job wordFilterJob;

    /**
     * Nightly scheduled run.
     * Cron configurable via {@code batch.scheduler.cron} in ai-filter-service.yml.
     */
    @Scheduled(cron = "${batch.scheduler.cron:0 0 2 * * ?}")
    public void scheduledRun() {
        log.info("Scheduled AI filter job triggered");
        launchJob(TriggerType.SCHEDULED);
    }

    /**
     * Launch the job programmatically (used by the scheduler and by
     * {@code FilterJobManagementService}).
     */
    public void launchJob(TriggerType triggerType) {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong(FilterConstants.PARAM_TRIGGERED_AT, System.currentTimeMillis())
                    .addString(FilterConstants.PARAM_TRIGGER_TYPE, triggerType.name())
                    .toJobParameters();

            log.info("Launching wordFilterJob with triggerType={}", triggerType);
            jobLauncher.run(wordFilterJob, params);

        } catch (Exception e) {
            log.error("Failed to launch wordFilterJob (triggerType={}): {}", triggerType, e.getMessage(), e);
        }
    }
}
