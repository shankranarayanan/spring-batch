package com.spring.batch.model;

import lombok.*;

@Data
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FileInputDTO {
    private int id;
    private String name;
    private int age;
    private String yearlySalary;
    private String loanTaken;
    private String address;
    private String typeOfJob;
}
