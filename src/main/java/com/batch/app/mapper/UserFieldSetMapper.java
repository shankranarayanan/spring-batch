package com.batch.app.mapper;

import com.batch.app.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Maps a Spring Batch {@link FieldSet} (a parsed flat-file row) to a {@link UserDTO}.
 * <p>
 * Used by TXT and CSV item readers for delimiter-based file formats.
 */
public class UserFieldSetMapper implements FieldSetMapper<UserDTO> {

    private static final Logger log = LoggerFactory.getLogger(UserFieldSetMapper.class);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Converts a parsed field set into a populated {@link UserDTO}.
     *
     * @param fieldSet the tokenized row fields from a flat file
     * @return a user DTO populated from the row data
     * @throws BindException if field binding fails at the framework level
     */
    @Override
    public UserDTO mapFieldSet(FieldSet fieldSet) throws BindException {
        UserDTO userDTO = new UserDTO();

        userDTO.setFirstName(fieldSet.readString("firstName"));
        userDTO.setLastName(fieldSet.readString("lastName"));

        try {
            String dobString = fieldSet.readString("dateOfBirth");
            userDTO.setDateOfBirth(LocalDate.parse(dobString, DATE_FORMATTER));
        } catch (Exception e) {
            log.error("Failed to parse dateOfBirth field: {}", fieldSet.readString("dateOfBirth"), e);
            userDTO.setDateOfBirth(null);
        }

        userDTO.setPhone(fieldSet.readString("phone"));
        userDTO.setEmail(fieldSet.readString("email"));
        userDTO.setAddress(fieldSet.readString("address"));
        userDTO.setCountry(fieldSet.readString("country"));

        return userDTO;
    }
}
