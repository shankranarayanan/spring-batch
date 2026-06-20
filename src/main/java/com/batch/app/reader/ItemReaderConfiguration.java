package com.batch.app.reader;

import com.batch.app.dto.UserDTO;
import com.batch.app.mapper.UserFieldSetMapper;
import com.batch.app.service.FileMonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.util.Optional;

/**
 * Spring configuration that defines all {@link ItemReader} beans for batch processing.
 * <p>
 * Each reader resolves the latest matching file from the monitored directory and
 * configures the appropriate delimiter or format parser for TXT, CSV, and XLSX inputs.
 */
@Configuration
public class ItemReaderConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ItemReaderConfiguration.class);

    private static final String[] FIELD_NAMES = {
            "firstName", "lastName", "dateOfBirth", "phone", "email", "address", "country"
    };

    private final FileMonitorService fileMonitorService;

    /**
     * Creates the item reader configuration with access to the file monitor service.
     *
     * @param fileMonitorService service that locates the latest batch input files
     */
    public ItemReaderConfiguration(FileMonitorService fileMonitorService) {
        this.fileMonitorService = fileMonitorService;
    }

    /**
     * Builds an {@link ItemReader} for pipe-delimited TXT batch files.
     * <p>
     * Returns {@code null} when no matching TXT file is found in the monitored directory.
     *
     * @return a configured flat-file reader, or {@code null} if no source file exists
     */
    @Bean
    public ItemReader<UserDTO> txtItemReader() {
        Optional<File> latestFile = fileMonitorService.getLatestTxtFile();

        if (latestFile.isEmpty()) {
            log.warn("No TXT batch file found in monitored directory");
            return null;
        }

        log.info("Configuring TXT item reader for file: {}", latestFile.get().getAbsolutePath());
        return buildFlatFileReader("txtItemReader", latestFile.get(), "|");
    }

    /**
     * Builds an {@link ItemReader} for comma-delimited CSV batch files.
     * <p>
     * Returns {@code null} when no matching CSV file is found in the monitored directory.
     *
     * @return a configured flat-file reader, or {@code null} if no source file exists
     */
    @Bean
    public ItemReader<UserDTO> csvItemReader() {
        Optional<File> latestFile = fileMonitorService.getLatestCsvFile();

        if (latestFile.isEmpty()) {
            log.warn("No CSV batch file found in monitored directory");
            return null;
        }

        log.info("Configuring CSV item reader for file: {}", latestFile.get().getAbsolutePath());
        return buildFlatFileReader("csvItemReader", latestFile.get(), DelimitedLineTokenizer.DELIMITER_COMMA);
    }

    /**
     * Builds an {@link ItemReader} for XLSX batch files using {@link ExcelFileReader}.
     * <p>
     * Returns {@code null} when no matching XLSX file is found or the reader cannot be opened.
     *
     * @return an Excel file reader, or {@code null} if no source file exists or opening fails
     */
    @Bean
    public ItemReader<UserDTO> xlsxItemReader() {
        Optional<File> latestFile = fileMonitorService.getLatestXlsxFile();

        if (latestFile.isEmpty()) {
            log.warn("No XLSX batch file found in monitored directory");
            return null;
        }

        try {
            log.info("Configuring XLSX item reader for file: {}", latestFile.get().getAbsolutePath());
            return new ExcelFileReader(latestFile.get());
        } catch (Exception e) {
            log.error("Failed to create Excel item reader for file: {}", latestFile.get().getAbsolutePath(), e);
            return null;
        }
    }

    /**
     * Constructs a {@link FlatFileItemReader} with the given delimiter and field mapping.
     *
     * @param readerName bean name for the reader instance
     * @param file       source file on the local filesystem
     * @param delimiter  column delimiter character or token
     * @return a fully configured flat-file item reader
     */
    private FlatFileItemReader<UserDTO> buildFlatFileReader(String readerName, File file, String delimiter) {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(delimiter);
        tokenizer.setNames(FIELD_NAMES);

        DefaultLineMapper<UserDTO> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new UserFieldSetMapper());

        return new FlatFileItemReaderBuilder<UserDTO>()
                .name(readerName)
                .resource(new FileSystemResource(file))
                .lineMapper(lineMapper)
                .linesToSkip(1)
                .build();
    }
}
