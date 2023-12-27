package com.logpose.ph2.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class LogposePh2ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogposePh2ApiApplication.class, args);
	}

}
