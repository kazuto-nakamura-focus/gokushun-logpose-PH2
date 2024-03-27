package com.logpose.ph2.api.configration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Configration
	{
	
	@Bean
	@ConfigurationProperties(prefix = "oauth")
	public DefaultOAuthParameters configDefaultOAuthParameters()
		{
		return new DefaultOAuthParameters();
		}

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

	@Bean
	@ConfigurationProperties(prefix = "sigfox")
	public DefaultSigFoxParameters configDefaultSigFoxParameters()
		{
		return new DefaultSigFoxParameters();
		}

	@Bean
	@ConfigurationProperties(prefix = "weather.query")
	public DefaultWeatherlAPIParameters configDefaultWeatherlAPIParameters()
		{
		return new DefaultWeatherlAPIParameters();
		}
	@Bean
	@ConfigurationProperties(prefix = "cors")
	public DefaultDomainParameter configDefaultDomainParameter()
		{
		return new DefaultDomainParameter();
		}
	/*@Bean
	public FilterRegistrationBean<AppAuthFilter> filter()
		{
		FilterRegistrationBean<AppAuthFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new AppAuthFilter(logicDomain, apiDomain));
		registrationBean.addUrlPatterns("/*");

		return registrationBean;
		}*/

	/*@Bean
	public ServletContextInitializer servletContextInitializer()
		{

		ServletContextInitializer servletContextInitializer = new ServletContextInitializer()
			{
			@Override
			public void onStartup(ServletContext servletContext) throws ServletException
				{
				servletContext.getSessionCookieConfig().setHttpOnly(true);
				servletContext.getSessionCookieConfig().setSecure(true);
				servletContext.setSessionTrackingModes(
						Collections.singleton(SessionTrackingMode.COOKIE));
				}
			};
		return servletContextInitializer;
		}*/
	}
