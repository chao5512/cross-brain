package com.deepthoughtdata;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Import;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableEurekaClient
@EnableSwagger2
public class AppModuleApplication {

    public static void main(String[] args) {
        // TODO Autoredis-server redis.conf-generated method stub
        new SpringApplicationBuilder(AppModuleApplication.class).run(args);
    }
}
