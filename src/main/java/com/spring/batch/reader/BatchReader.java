package com.spring.batch.reader;

import com.spring.batch.model.FileInputDTO;
import com.spring.batch.model.FileInputModel;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class BatchReader {
    public ItemReader<? extends FileInputModel> readFlatFile() {
        FlatFileItemReader<FileInputModel> itemReader = new FlatFileItemReader<FileInputModel>();
        itemReader.setResource(new ClassPathResource("data.txt"));
        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }

    private LineMapper<FileInputModel> lineMapper() {
        DefaultLineMapper<FileInputModel> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter("|");
        lineTokenizer.setNames("id", "name", "age", "yearlySalary", "loanTaken", "address", "typeOfJob");
        lineTokenizer.setStrict(false);
        BeanWrapperFieldSetMapper<FileInputModel> fieldSetMapper = new BeanWrapperFieldSetMapper<FileInputModel>();
        fieldSetMapper.setTargetType(FileInputModel.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }
}
