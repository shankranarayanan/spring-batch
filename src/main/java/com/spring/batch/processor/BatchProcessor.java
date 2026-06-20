package com.spring.batch.processor;

import com.spring.batch.model.FileInputDTO;
import com.spring.batch.model.FileInputModel;
import org.apache.logging.log4j.util.Strings;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class BatchProcessor implements ItemProcessor<FileInputModel, FileInputDTO> {
    @Override
    public FileInputDTO process(FileInputModel item) throws Exception {

        FileInputDTO fileDTO = new FileInputDTO();
        fileDTO.setId(Integer.parseInt(item.getId()));
        fileDTO.setName(Strings.toRootUpperCase(item.getName()));
        fileDTO.setAge(Integer.parseInt(item.getAge()));
        fileDTO.setAddress(item.getAddress());
        fileDTO.setYearlySalary(item.getYearlySalary());
        fileDTO.setLoanTaken(item.getLoanTaken());
        fileDTO.setTypeOfJob(item.getTypeOfJob());
        return fileDTO;
    }
}
