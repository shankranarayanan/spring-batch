package com.spring.batch.config;

import com.spring.batch.model.CSVInputModel;
import com.spring.batch.processor.CSVInputProcessor;
import com.spring.batch.reader.CSVInputReader;
import com.spring.batch.writer.CSVInputWriter;
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
public class CSVSpringBatchConfig {

    @Autowired
    CSVInputWriter csvInputWriter;

    public CSVInputProcessor csvInputProcessor(){
        return new CSVInputProcessor();
    }

    @Autowired
    CSVInputReader csvInputReader;

    @Bean
    public Job runCSVInputJob(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new JobBuilder("CSVInputJob", jobRepository).flow(csvInputStep(jobRepository, platformTransactionManager)).end().build();
    }

    private Step csvInputStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("CSVInputStep", jobRepository)
                .<CSVInputModel, CSVInputModel >chunk(10, transactionManager)
                .reader(csvInputReader.getCSVRecords())
                .processor(csvInputProcessor())
                .writer(csvInputWriter)
                .build();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new ResourcelessTransactionManager(); // For simple batch processing without needing a real database transaction manager.
    }
}
