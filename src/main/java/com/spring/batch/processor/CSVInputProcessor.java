package com.spring.batch.processor;

import com.spring.batch.model.CSVInputModel;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class CSVInputProcessor implements ItemProcessor<CSVInputModel, CSVInputModel> {
    /**
     * @param item
     * @return
     * @throws Exception
     */
    @Override
    public CSVInputModel process(CSVInputModel item) {
        int id = item.getId();
        int age = item.getAge();
        String firstName = item.getFirstName().toUpperCase();
        String lastName = item.getLastName().toUpperCase();
        return new CSVInputModel(id, firstName, lastName, age);
    }
}
