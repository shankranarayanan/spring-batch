package com.spring.batch.config;

import com.spring.batch.model.FileInputDTO;
import com.spring.batch.model.FileInputModel;
import com.spring.batch.processor.BatchProcessor;
import com.spring.batch.reader.BatchReader;
import com.spring.batch.writer.BatchWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
public class SpringBatchConfig {

    @Autowired
    BatchReader batchReader;

    @Autowired
    BatchWriter batchWriter;

    @Bean
    public BatchProcessor processor(){
        return new BatchProcessor();
    }

    @Bean
    public Job runBatchJob(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new JobBuilder("BatchJob", jobRepository).flow(batchStep(jobRepository, platformTransactionManager)).end().build();
    }

    private Step batchStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("batchStep", jobRepository)
                .<FileInputModel, FileInputDTO>chunk(10, platformTransactionManager)
                .reader(batchReader.readFlatFile())
                .processor(processor())
                .writer(batchWriter)
                .build();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new ResourcelessTransactionManager(); // For simple batch processing without needing a real database transaction manager.
    }


}
