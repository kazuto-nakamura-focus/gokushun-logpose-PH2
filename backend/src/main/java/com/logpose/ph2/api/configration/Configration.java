package com.logpose.ph2.api.configration;

import java.util.Collections;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.SessionTrackingMode;

@Configuration
public class Configration
	{
	@Bean
	@ConfigurationProperties(prefix = "oauth")
    DefaultOAuthParameters configDefaultOAuthParameters()
		{
		return new DefaultOAuthParameters();
		}

	@Bean
	@ConfigurationProperties(prefix = "paramset.default.growth")
	DefaultGrowthParameters configUrl()
		{
		return new DefaultGrowthParameters();
		}

	@Bean
	@ConfigurationProperties(prefix = "paramset.default.fstage")
	DefaultFtageValues configFStage()
		{
		return new DefaultFtageValues();
		}

	@Bean
	@ConfigurationProperties(prefix = "paramset.default.leafarea.area")
	DefaultLeafAreaParameters configLeafAreaParameters()
		{
		return new DefaultLeafAreaParameters();
		}

	@Bean
	@ConfigurationProperties(prefix = "paramset.default.leafarea.count")
	DefaultLeafCountParameters configLeafCountParameters()
		{
		return new DefaultLeafCountParameters();
		}

	@Bean
	@ConfigurationProperties(prefix = "paramset.default.ps")
	DefaultPsParameters configLeafPsParameters()
		{
		return new DefaultPsParameters();
		}

	@Bean
	@ConfigurationProperties(prefix = "sigfox")
	DefaultSigFoxParameters configDefaultSigFoxParameters()
		{
		return new DefaultSigFoxParameters();
		}

	@Bean
	@ConfigurationProperties(prefix = "weather.query")
	DefaultWeatherlAPIParameters configDefaultWeatherlAPIParameters()
		{
		return new DefaultWeatherlAPIParameters();
		}
	/*@Bean
	FilterRegistrationBean<AppAuthFilter> filter()
		{
		FilterRegistrationBean<AppAuthFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new AppAuthFilter(logicDomain, apiDomain));
		registrationBean.addUrlPatterns("/*");

		return registrationBean;
		}*/

@Bean
	ServletContextInitializer servletContextInitializer()
		{

		ServletContextInitializer servletContextInitializer = new ServletContextInitializer()
			{
			@Override
			public void onStartup(ServletContext servletContext) throws ServletException
				{
			servletContext.getSessionCookieConfig().setHttpOnly(false);
				servletContext.getSessionCookieConfig().setSecure(true);
				servletContext.setSessionTrackingModes(
						Collections.singleton(SessionTrackingMode.COOKIE));
				}
			};
		return servletContextInitializer;
		}
	}
