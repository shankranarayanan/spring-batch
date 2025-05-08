package com.spring.batch.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "CSVInput")
public class CSVInputModel {

    @Id
    private int id;
    private String firstName;
    private String lastName;
    private int age;


}
