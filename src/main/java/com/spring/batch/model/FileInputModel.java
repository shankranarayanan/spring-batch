package com.spring.batch.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Data
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Customers")
public class FileInputModel {
    @Id
    private String id;
    private String name;
    private String age;
    @Column(name = "salary")
    private String yearlySalary;
    @Column(name = "loan")
    private String loanTaken;
    private String address;
    @Column(name = "job_type")
    private String typeOfJob;
}
