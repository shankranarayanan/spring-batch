package com.batch.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Entry point for the Spring Batch multi-format file processing application.
 * <p>
 * Supports scheduled ingestion of TXT, CSV, and XLSX user records into a MySQL database.
 */
@SpringBootApplication
@EnableScheduling
public class SpringBatchApplication {

    /**
     * Bootstraps the Spring application context.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(SpringBatchApplication.class, args);
    }
}
