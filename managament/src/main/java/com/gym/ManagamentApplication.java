package com.gym;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ManagamentApplication {

	public static void main(String[] args) {
		SpringApplication.run(ManagamentApplication.class, args);
	}

}
