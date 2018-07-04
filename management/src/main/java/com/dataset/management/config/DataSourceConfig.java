package com.dataset.management.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@ConfigurationProperties(prefix = "spring.secondary-datasource")
@EnableAutoConfiguration
public class DataSourceConfig {

    @Value("${spring.secondary-datasource.url}")
    private String dbUrl;
    @Value("${spring.secondary-datasource.username}")
    private String dbUser;
    @Value("${spring.secondary-datasource.password}")
    private String dbPassword;
    @Value("${spring.secondary-datasource.driver-class-name}")
    private String driverClassName;

    @Bean(name = "secondaryDataSource")
    public DataSource primaryDataSource(){
        DataSource dataSource = DataSourceBuilder.create().driverClassName(driverClassName)
                .url(dbUrl)
                .username(dbUser)
                .password(dbPassword).build();
        return dataSource;
    }
    @Bean(name = "secondaryJdbcTemplate")
    public JdbcTemplate primaryJdbcTemplate(@Qualifier("secondaryDataSource") DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }
}
