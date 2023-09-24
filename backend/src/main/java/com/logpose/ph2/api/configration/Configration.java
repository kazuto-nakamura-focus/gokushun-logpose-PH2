package com.logpose.ph2.api.configration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Configration
	{
	@Bean
	@ConfigurationProperties(prefix = "paramset.default.growth")
	public DefaultGrowthParameters configUrl()
		{
		return new DefaultGrowthParameters();
		}
	
	@Bean
	@ConfigurationProperties(prefix = "paramset.default.fstage")
	public DefaultFtageValues configFStage()
		{
		return new DefaultFtageValues();
		}
	
	@Bean
	@ConfigurationProperties(prefix = "paramset.default.leafarea.area")
	public DefaultLeafAreaParameters configLeafAreaParameters()
		{
		return new DefaultLeafAreaParameters();
		}
	
	@Bean
	@ConfigurationProperties(prefix = "paramset.default.leafarea.count")
	public DefaultLeafCountParameters configLeafCountParameters()
		{
		return new DefaultLeafCountParameters();
		}
	
	@Bean
	@ConfigurationProperties(prefix = "paramset.default.ps")
	public DefaultPsParameters configLeafPsParameters()
		{
		return new DefaultPsParameters();
		}
	}
