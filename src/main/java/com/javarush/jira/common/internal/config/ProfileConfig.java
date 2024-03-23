package com.javarush.jira.common.internal.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class ProfileConfig {

    @Bean
    @Profile("prod")
    public DataSource prodDataSource() {
        // Define production data source configuration
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/jira");
        dataSource.setUsername("jira");
        dataSource.setPassword("JiraRush");
        return dataSource;
    }

    @Bean
    @Profile("test")
    public DataSource testDataSource() {
        // Define test data source configuration
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:testdb");
        dataSource.setUsername("");
        dataSource.setPassword("");
        return dataSource;
    }
}
