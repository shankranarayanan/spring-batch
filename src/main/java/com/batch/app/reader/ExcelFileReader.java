package com.batch.app.reader;

import com.batch.app.dto.UserDTO;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * {@link ItemReader} implementation that reads user records from an Excel XLSX workbook.
 * <p>
 * Expects a header row on the first line and reads subsequent rows into {@link UserDTO}
 * instances using a fixed column order.
 */
public class ExcelFileReader implements ItemReader<UserDTO> {

    private static final Logger log = LoggerFactory.getLogger(ExcelFileReader.class);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final Workbook workbook;
    private final Sheet sheet;
    private int currentRowIndex = 1;

    /**
     * Opens the given XLSX file and prepares the first worksheet for reading.
     *
     * @param file the Excel file to read
     * @throws Exception if the workbook cannot be opened or parsed
     */
    public ExcelFileReader(File file) throws Exception {
        this.workbook = new XSSFWorkbook(new FileInputStream(file));
        this.sheet = workbook.getSheetAt(0);
        log.debug("Opened Excel workbook: {} (sheet rows: {})", file.getName(), sheet.getLastRowNum());
    }

    /**
     * Reads the next row from the worksheet and maps it to a {@link UserDTO}.
     *
     * @return the next user record, or {@code null} when all rows have been consumed
     * @throws ParseException if a row cannot be parsed into a valid DTO
     */
    @Override
    public UserDTO read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        Row row = sheet.getRow(currentRowIndex);

        if (row == null) {
            workbook.close();
            log.debug("Finished reading Excel file at row index {}", currentRowIndex);
            return null;
        }

        UserDTO userDTO = new UserDTO();

        try {
            userDTO.setFirstName(getCellValue(row, 0));
            userDTO.setLastName(getCellValue(row, 1));

            String dobString = getCellValue(row, 2);
            if (dobString != null && !dobString.isEmpty()) {
                userDTO.setDateOfBirth(LocalDate.parse(dobString, DATE_FORMATTER));
            }

            userDTO.setPhone(getCellValue(row, 3));
            userDTO.setEmail(getCellValue(row, 4));
            userDTO.setAddress(getCellValue(row, 5));
            userDTO.setCountry(getCellValue(row, 6));

            currentRowIndex++;
            return userDTO;
        } catch (Exception e) {
            currentRowIndex++;
            log.error("Failed to parse Excel row at index {}", currentRowIndex, e);
            throw new ParseException("Error parsing Excel row " + currentRowIndex, e);
        }
    }

    /**
     * Safely extracts a string value from the given cell, returning {@code null} on failure.
     *
     * @param row         the Excel row containing the cell
     * @param columnIndex zero-based column index
     * @return the cell value as a string, or {@code null} if the cell is missing or unreadable
     */
    private String getCellValue(Row row, int columnIndex) {
        try {
            return row.getCell(columnIndex).getStringCellValue();
        } catch (Exception e) {
            log.debug("Unable to read cell at column {} in row {}: {}", columnIndex, currentRowIndex, e.getMessage());
            return null;
        }
    }
}
