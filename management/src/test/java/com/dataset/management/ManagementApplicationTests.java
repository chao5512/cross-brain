package com.dataset.management;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootApplication
public class ManagementApplicationTests {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new SpringApplicationBuilder(ManagementApplicationTests.class).run(args);
	}
}
