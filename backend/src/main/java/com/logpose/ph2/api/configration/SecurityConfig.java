package com.logpose.ph2.api.configration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig
	{
	@Bean
	public  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
		{
		http.cors();
		http
				.authorizeHttpRequests(authz -> {
					try
						{
						authz
						.requestMatchers("/error", "/webjars/**").permitAll()
						.anyRequest().authenticated()
						.and()
						.oauth2Login(oauth2Login ->oauth2Login.defaultSuccessUrl("http://localhost:8080/", true).failureUrl("/login?error=true"))
						.logout()
						.logoutSuccessUrl("/");
						}
					catch (Exception e)
						{
						e.printStackTrace();
						}
					});
		 return http.build();
		}
	}