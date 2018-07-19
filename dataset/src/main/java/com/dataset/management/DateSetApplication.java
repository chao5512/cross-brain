package com.dataset.management;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableEurekaClient
@EnableSwagger2

public class DateSetApplication {
	public static void main(String[] args) {
		SpringApplication.run(DateSetApplication.class, args);
	}
}
