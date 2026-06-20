package com.batch.app.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

/**
 * Listener that logs batch job lifecycle events including start, completion, and failure.
 * <p>
 * Failures are logged at ERROR level with full exception details for troubleshooting.
 */
@Component
public class BatchJobListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(BatchJobListener.class);

    /**
     * Logs the start of a batch job execution.
     *
     * @param jobExecution the current job execution context
     */
    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Starting batch job: {}", jobExecution.getJobInstance().getJobName());
    }

    /**
     * Logs the outcome of a batch job including record counts or failure details.
     *
     * @param jobExecution the completed job execution context
     */
    @Override
    public void afterJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            long recordsProcessed = jobExecution.getStepExecutions().stream()
                    .mapToLong(execution -> execution.getReadCount())
                    .sum();
            log.info("Batch job completed successfully: {} — records processed: {}", jobName, recordsProcessed);
        } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
            log.error("Batch job failed: {} — status: {}", jobName, jobExecution.getStatus());
            jobExecution.getAllFailureExceptions()
                    .forEach(exception -> log.error("Batch job failure cause for {}: {}", jobName, exception.getMessage(), exception));
        } else {
            log.warn("Batch job finished with status {}: {}", jobExecution.getStatus(), jobName);
        }
    }
}
