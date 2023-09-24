package com.logpose.ph2.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LogposePh2BatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogposePh2BatchApplication.class, args);
	}

}
