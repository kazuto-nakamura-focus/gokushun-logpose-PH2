package com.logpose.ph2.batch.configraion;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Configration
	{
	@Bean
	@ConfigurationProperties(prefix = "loghorse.batch.api")
	public DefaultUrlParameters configUrl()
		{
		return new DefaultUrlParameters();
		}
	}
