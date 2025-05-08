package com.spring.batch.writer;

import com.spring.batch.model.CSVInputModel;
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
public class CSVInputWriter implements ItemWriter<CSVInputModel> {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    /**
     *
     * @param chunk
     * @throws Exception
     */
    @Override
    public void write(Chunk<? extends CSVInputModel> chunk) throws Exception {
        String insertQuery = "INSERT INTO CSVInput (id, age, first_name, last_name) VALUES (?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(insertQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                CSVInputModel item = chunk.getItems().get(i);
                ps.setInt(1, item.getId());
                ps.setInt(2, item.getAge());
                ps.setString(3, item.getFirstName());
                ps.setString(4, item.getLastName());
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
