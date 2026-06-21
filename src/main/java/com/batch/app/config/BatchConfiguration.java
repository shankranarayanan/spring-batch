package com.batch.app.config;

import com.batch.app.dto.UserDTO;
import com.batch.app.entity.User;
import com.batch.app.processor.UserItemProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Spring Batch job and step configuration for TXT, CSV, and XLSX file processing.
 * <p>
 * Item readers are defined in {@link com.batch.app.reader.ItemReaderConfiguration}
 * and injected here to keep batch wiring separate from reader construction.
 */
@Configuration
public class BatchConfiguration {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final UserItemProcessor userItemProcessor;
    private final ItemReader<UserDTO> txtItemReader;
    private final ItemReader<UserDTO> csvItemReader;
    private final ItemReader<UserDTO> xlsxItemReader;

    /**
     * Creates batch configuration with injected job infrastructure and item readers.
     *
     * @param jobRepository        Spring Batch job repository
     * @param transactionManager   platform transaction manager for chunk processing
     * @param userItemProcessor    processor that maps DTOs to persisted entities
     * @param txtItemReader        reader for pipe-delimited TXT files
     * @param csvItemReader        reader for comma-delimited CSV files
     * @param xlsxItemReader       reader for Excel XLSX files
     */
    public BatchConfiguration(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            UserItemProcessor userItemProcessor,
            @Qualifier("txtItemReader") ItemReader<UserDTO> txtItemReader,
            @Qualifier("csvItemReader") ItemReader<UserDTO> csvItemReader,
            @Qualifier("xlsxItemReader") ItemReader<UserDTO> xlsxItemReader) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.userItemProcessor = userItemProcessor;
        this.txtItemReader = txtItemReader;
        this.csvItemReader = csvItemReader;
        this.xlsxItemReader = xlsxItemReader;
    }

    /**
     * Defines the chunk-based step for TXT file batch processing.
     *
     * @return the configured TXT batch step
     */
    @Bean
    public Step txtBatchStep() {
        return new StepBuilder("txtBatchStep", jobRepository)
                .<UserDTO, User>chunk(10, transactionManager)
                .reader(txtItemReader)
                .processor(userItemProcessor)
                .writer(items -> {})
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(10)
                .build();
    }

    /**
     * Defines the batch job that executes the TXT processing step.
     *
     * @return the configured TXT batch job
     */
    @Bean
    public Job txtBatchJob() {
        return new JobBuilder("txtBatchJob", jobRepository)
                .start(txtBatchStep())
                .build();
    }

    /**
     * Defines the chunk-based step for CSV file batch processing.
     *
     * @return the configured CSV batch step
     */
    @Bean
    public Step csvBatchStep() {
        return new StepBuilder("csvBatchStep", jobRepository)
                .<UserDTO, User>chunk(10, transactionManager)
                .reader(csvItemReader)
                .processor(userItemProcessor)
                .writer(items -> {})
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(10)
                .build();
    }

    /**
     * Defines the batch job that executes the CSV processing step.
     *
     * @return the configured CSV batch job
     */
    @Bean
    public Job csvBatchJob() {
        return new JobBuilder("csvBatchJob", jobRepository)
                .start(csvBatchStep())
                .build();
    }

    /**
     * Defines the chunk-based step for XLSX file batch processing.
     *
     * @return the configured XLSX batch step
     */
    @Bean
    public Step xlsxBatchStep() {
        return new StepBuilder("xlsxBatchStep", jobRepository)
                .<UserDTO, User>chunk(10, transactionManager)
                .reader(xlsxItemReader)
                .processor(userItemProcessor)
                .writer(items -> {})
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(10)
                .build();
    }

    /**
     * Defines the batch job that executes the XLSX processing step.
     *
     * @return the configured XLSX batch job
     */
    @Bean
    public Job xlsxBatchJob() {
        return new JobBuilder("xlsxBatchJob", jobRepository)
                .start(xlsxBatchStep())
                .build();
    }
}
