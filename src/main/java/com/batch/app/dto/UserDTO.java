package com.batch.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Data transfer object representing a user record parsed from a batch input file.
 * <p>
 * Used as the item type for all TXT, CSV, and XLSX item readers before processing.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String phone;
    private String email;
    private String address;
    private String country;
}
