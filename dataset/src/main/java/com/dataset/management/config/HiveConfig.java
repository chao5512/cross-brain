package com.dataset.management.config;
import org.apache.hive.jdbc.HiveDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

/**
 * @ClassName HiveConfig
 * @Description hive配置类
 * @Auther: 王培文
 * @Date: 2018/6/5
 * @Version 1.0
 **/
@Configuration
public class HiveConfig {
    @Value("${hive.url}")
    private String hiveUrl;
    @Value("${hive.port}")
    private String hivePort;
    @Value("${hive.schema}")
    private String hiveSchema;

    @Bean(name="hiveJdbcTemplate")
    JdbcTemplate getHiveJdbcTemplate(){

        String connUrl = hiveUrl + ":" +hivePort + "/" + hiveSchema;
        DataSource hiveDataSource = new SimpleDriverDataSource(new HiveDriver(), connUrl);
        return new JdbcTemplate(hiveDataSource);
    }
}
