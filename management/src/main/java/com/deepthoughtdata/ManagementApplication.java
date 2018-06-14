package com.deepthoughtdata;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableEurekaClient
@EnableSwagger2
public class ManagementApplication {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        new SpringApplicationBuilder(ManagementApplication.class).run(args);
    }
}
