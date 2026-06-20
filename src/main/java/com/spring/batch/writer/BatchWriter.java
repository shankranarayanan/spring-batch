package com.spring.batch.writer;

import com.spring.batch.model.FileInputDTO;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Component
public class BatchWriter implements ItemWriter<FileInputDTO> {

    @Autowired
    DataSource datasource;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private final String insertQuery = "INSERT INTO Customers (id, age, name, salary, loan, address, job_type) VALUES (?, ?, ?, ?, ?, ?, ?)";


    @Override
    public void write(Chunk<? extends FileInputDTO> chunk) throws Exception {
        jdbcTemplate.batchUpdate(insertQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                FileInputDTO item = chunk.getItems().get(i);
                ps.setInt(1, item.getId());
                ps.setInt(2, item.getAge());
                ps.setString(3, item.getName());
                ps.setString(4, item.getYearlySalary());
                ps.setString(5, item.getLoanTaken());
                ps.setString(6, item.getAddress());
                ps.setString(7, item.getTypeOfJob());
            }

            /**
             * @return
             */
            @Override
            public int getBatchSize() {
                return chunk.getItems().size();
            }
        });
    }
}
