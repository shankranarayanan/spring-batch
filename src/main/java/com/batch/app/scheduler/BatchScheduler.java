package com.batch.app.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Scheduler that triggers TXT, CSV, and XLSX batch jobs at configured cron intervals.
 * <p>
 * Each job is launched with a unique parameter set to satisfy Spring Batch restart constraints.
 */
@Component
public class BatchScheduler {

    private static final Logger log = LoggerFactory.getLogger(BatchScheduler.class);

    private final JobLauncher jobLauncher;
    private final Optional<Job> txtBatchJob;
    private final Optional<Job> csvBatchJob;
    private final Optional<Job> xlsxBatchJob;

    /**
     * Creates the scheduler with the job launcher and optional batch job beans.
     *
     * @param jobLauncher  launcher used to start batch jobs
     * @param txtBatchJob  optional TXT batch job bean
     * @param csvBatchJob  optional CSV batch job bean
     * @param xlsxBatchJob optional XLSX batch job bean
     */
    public BatchScheduler(
            JobLauncher jobLauncher,
            @Qualifier("txtBatchJob") Optional<Job> txtBatchJob,
            @Qualifier("csvBatchJob") Optional<Job> csvBatchJob,
            @Qualifier("xlsxBatchJob") Optional<Job> xlsxBatchJob) {
        this.jobLauncher = jobLauncher;
        this.txtBatchJob = txtBatchJob;
        this.csvBatchJob = csvBatchJob;
        this.xlsxBatchJob = xlsxBatchJob;
    }

    /**
     * Schedules the TXT batch job daily at 20:00 IST.
     */
    @Scheduled(cron = "0 0 20 * * ?", zone = "Asia/Kolkata")
    public void scheduleTxtBatch() {
        txtBatchJob.ifPresent(job -> executeBatch(job, "TXT"));
    }

    /**
     * Schedules the CSV batch job daily at 20:30 IST.
     */
    @Scheduled(cron = "0 30 20 * * ?", zone = "Asia/Kolkata")
    public void scheduleCsvBatch() {
        csvBatchJob.ifPresent(job -> executeBatch(job, "CSV"));
    }

    /**
     * Schedules the XLSX batch job daily at 21:00 IST.
     */
    @Scheduled(cron = "0 0 21 * * ?", zone = "Asia/Kolkata")
    public void scheduleXlsxBatch() {
        xlsxBatchJob.ifPresent(job -> executeBatch(job, "XLSX"));
    }

    /**
     * Launches the given batch job with a unique job parameter identifier.
     *
     * @param job      the batch job to execute
     * @param fileType human-readable file type label used in logging and parameters
     */
    private void executeBatch(Job job, String fileType) {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("jobId", fileType + "_" + LocalDateTime.now())
                    .toJobParameters();

            jobLauncher.run(job, jobParameters);
            log.info("Started {} batch job at {}", fileType, LocalDateTime.now());
        } catch (Exception e) {
            log.error("Failed to execute {} batch job", fileType, e);
        }
    }
}
