package com.spring.batch.reader;

import com.spring.batch.model.CSVInputModel;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

@Component
public class CSVInputReader {

    public FlatFileItemReader<CSVInputModel> getCSVRecords() {
        FlatFileItemReader<CSVInputModel> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/students.csv"));
        itemReader.setName("csvReader");//some unique name
        itemReader.setLinesToSkip(1);//skip reading the header
        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }

    private LineMapper<CSVInputModel> lineMapper() {
        DefaultLineMapper<CSVInputModel> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(","); //for csv, its comma delimited
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "firstname", "lastname", "age");
        //this maps each row that is read from the csv to a student object
        BeanWrapperFieldSetMapper<CSVInputModel> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(CSVInputModel.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

}
