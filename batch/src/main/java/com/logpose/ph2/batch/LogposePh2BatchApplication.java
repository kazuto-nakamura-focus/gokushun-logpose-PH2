package com.logpose.ph2.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.logpose.ph2.batch.controller.DeviceDataLoader;

@SpringBootApplication
@EnableScheduling
public class LogposePh2BatchApplication {

	public static void main(String[] args) {
		 ConfigurableApplicationContext ctx = SpringApplication.run(LogposePh2BatchApplication.class, args);
		 DeviceDataLoader service = ctx.getBean(DeviceDataLoader.class);
		 
		 service.load();
	}

}
