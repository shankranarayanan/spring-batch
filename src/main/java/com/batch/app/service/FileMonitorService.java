package com.batch.app.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Service that locates the most recently modified batch input file matching a given pattern.
 * <p>
 * Monitors a configured directory for TXT, CSV, and XLSX files whose names match
 * timestamp-based naming conventions.
 */
@Service
public class FileMonitorService {

    private static final Logger log = LoggerFactory.getLogger(FileMonitorService.class);

    @Value("${file.monitor.path}")
    private String monitorPath;

    @Value("${file.pattern.txt}")
    private String txtPattern;

    @Value("${file.pattern.csv}")
    private String csvPattern;

    @Value("${file.pattern.xlsx}")
    private String xlsxPattern;

    /**
     * Finds the latest TXT file matching the configured naming pattern.
     *
     * @return the most recently modified matching file, or empty if none found
     */
    public Optional<File> getLatestTxtFile() {
        return getLatestFile(txtPattern);
    }

    /**
     * Finds the latest CSV file matching the configured naming pattern.
     *
     * @return the most recently modified matching file, or empty if none found
     */
    public Optional<File> getLatestCsvFile() {
        return getLatestFile(csvPattern);
    }

    /**
     * Finds the latest XLSX file matching the configured naming pattern.
     *
     * @return the most recently modified matching file, or empty if none found
     */
    public Optional<File> getLatestXlsxFile() {
        return getLatestFile(xlsxPattern);
    }

    /**
     * Scans the monitored directory and returns the newest file matching the given regex pattern.
     *
     * @param pattern regular expression matched against file names
     * @return the most recently modified matching file, or empty if the directory is missing or unreadable
     */
    private Optional<File> getLatestFile(String pattern) {
        try {
            Path dirPath = Paths.get(monitorPath);

            if (!Files.exists(dirPath)) {
                log.warn("Monitor directory does not exist: {}", monitorPath);
                return Optional.empty();
            }

            Pattern filePattern = Pattern.compile(pattern);

            return Files.list(dirPath)
                    .filter(path -> path.toFile().isFile())
                    .filter(path -> filePattern.matcher(path.getFileName().toString()).matches())
                    .max((p1, p2) -> {
                        try {
                            long t1 = Files.getLastModifiedTime(p1).toMillis();
                            long t2 = Files.getLastModifiedTime(p2).toMillis();
                            return Long.compare(t1, t2);
                        } catch (IOException e) {
                            log.error("Failed to compare last-modified times for files in {}", monitorPath, e);
                            return 0;
                        }
                    })
                    .map(Path::toFile);
        } catch (IOException e) {
            log.error("Failed to list files in monitor directory: {}", monitorPath, e);
            return Optional.empty();
        }
    }
}
