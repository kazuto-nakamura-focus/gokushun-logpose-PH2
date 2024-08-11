package com.logpose.ph2.api.security;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;
import org.springframework.web.cors.CorsConfiguration;

import com.logpose.ph2.api.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.logpose.ph2.api.security.oauth2.handler.OAuth2AuthenticationFailureHandler;
import com.logpose.ph2.api.security.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import com.logpose.ph2.api.security.oauth2.service.CustomOAuth2UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final Logger LOG = LogManager.getLogger(SecurityConfig.class);

    @Value("${spring.security.oauth2.client.registration.heroku.redirect-uri}")
    private String redirectUri;

    @Value("${frontend.after-logout-uri}")
    private String redirectAfterLogoutUri;

    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOauth2AuthorizationRequestRepository;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oauth2AuthenticationFailureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(request -> {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(List.of("http://localhost:8080", "http://localhost:3000", "https://gokushun-ph2-it.herokuapp.com", "https://gokushun-ph2-staging-e2e7adc0c3d1.herokuapp.com"));
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
                config.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
                config.setAllowCredentials(true);
                return config;
            }))
            .csrf(AbstractHttpConfigurer::disable)
            .headers(headersConfigurer -> headersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests((requests) ->
                requests
                    .requestMatchers("/api/auth/**", "/api/bulk/update").permitAll()
                    .anyRequest().authenticated()
            )
            .rememberMe((rememberMe) -> rememberMe
                .rememberMeServices(rememberMeServices())
            )
            .sessionManagement(sessions -> sessions.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .oauth2Login(configure ->
                configure
                    .authorizationEndpoint(config -> config
                        .authorizationRequestRepository(httpCookieOauth2AuthorizationRequestRepository)
                        .baseUri("/api/oauth2/authorization"))
                    .userInfoEndpoint(config -> config.userService(customOAuth2UserService))
                    .successHandler(oauth2AuthenticationSuccessHandler)
                    .failureHandler(oauth2AuthenticationFailureHandler)
                    .loginProcessingUrl("/api/login/oauth2/code/*")
            )
            .logout(configure ->
                configure
                    .logoutUrl("/api/logout")
                    .deleteCookies("JSESSIONID", "SESSION", "TOKEN")
                    .invalidateHttpSession(true)
                    .logoutSuccessUrl(redirectAfterLogoutUri)
                    .clearAuthentication(true)
            )
            .exceptionHandling(exceptionHandling ->
                exceptionHandling.authenticationEntryPoint(new Http401AuthenticationEntryPoint())
            );

        return http.build();
    }

    @Bean
    public SpringSessionRememberMeServices rememberMeServices() {
        SpringSessionRememberMeServices rememberMeServices = new SpringSessionRememberMeServices();
        rememberMeServices.setAlwaysRemember(true);
        return rememberMeServices;
    }

    public static class Http401AuthenticationEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "認証が必要です");
        }
    }
}
