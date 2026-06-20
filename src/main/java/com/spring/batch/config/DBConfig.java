package com.spring.batch.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DBConfig {

    @Bean
    public DataSource dataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver");
        dataSourceBuilder.url("jdbc:mysql://localhost:3306/SprBtch?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC");
        dataSourceBuilder.username("root");
        dataSourceBuilder.password("Fluffy@1");
        return dataSourceBuilder.build();
    }

}
