package com.spring.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableBatchProcessing
@SpringBootApplication
@EnableScheduling
public class SpringBatchApplication implements CommandLineRunner {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    public static void main(String[] args){
        new SpringApplicationBuilder(SpringBatchApplication.class).web(WebApplicationType.NONE).run(args);
    }

    /**
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
    scheduleCSVJob();
    }

    private void scheduleCSVJob() throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, JobParametersInvalidException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("CSV", System.currentTimeMillis())  // unique parameter
                .toJobParameters();
        jobLauncher.run(job, jobParameters);
        System.out.println("CSV Batch job has been invoked.");
    }
}
