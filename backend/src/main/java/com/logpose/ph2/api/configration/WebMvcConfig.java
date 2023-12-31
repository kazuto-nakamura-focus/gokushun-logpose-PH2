package com.logpose.ph2.api.configration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
  @Override
  public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/*")
				.allowedOrigins("http://localhost:8080", "https://gokushun-ph2-it.herokuapp.com")
				.allowedMethods("GET", "PUT", "DELETE", "POST")
				.allowedHeaders("X-Requested-With, X-HTTP-Method-Override, Content-Type, Accept")
				.allowCredentials(true);
		}
	}
