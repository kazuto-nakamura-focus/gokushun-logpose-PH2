package com.logpose.ph2.api.security;

import com.logpose.ph2.api.security.jwt.JwtAuthorizationFilter;
import com.logpose.ph2.api.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.logpose.ph2.api.security.oauth2.handler.OAuth2AuthenticationFailureHandler;
import com.logpose.ph2.api.security.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import com.logpose.ph2.api.security.oauth2.service.CustomOAuth2UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static Logger LOG = LogManager.getLogger(SecurityConfig.class);

    @Value("${spring.security.oauth2.client.registration.heroku.redirect-uri}")
    private String redirectUri;

    @Autowired
    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOauth2AuthorizationRequestRepository;
    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;
    @Autowired
    private OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler;
    @Autowired
    private OAuth2AuthenticationFailureHandler oauth2AuthenticationFailureHandler;
    @Autowired
    private JwtAuthorizationFilter jwtAuthorizationFilter;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headersConfigurer -> headersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((requests) ->
                        requests
                                .requestMatchers("/api/auth/**").permitAll()
                                .anyRequest().authenticated()
                )
//                .sessionManagement(sessions -> sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .oauth2ResourceServer(configure -> configure.jwt(Customizer.withDefaults()));
//                .oauth2Login(Customizer.withDefaults());
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
                                .logoutRequestMatcher(new AntPathRequestMatcher("/api/logout"))
                                .deleteCookies("JSESSIONID")
                                .invalidateHttpSession(true)
                                .logoutSuccessUrl(redirectUri)
                                .clearAuthentication(true)  // 認証情報をクリア
                );
//                .exceptionHandling(exceptionHandling ->
//                        exceptionHandling.authenticationEntryPoint(new Http401AuthenticationEntryPoint())
//                );

//        http.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    public class Http401AuthenticationEntryPoint implements AuthenticationEntryPoint {

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
//            LOG.info("Commence:"+request.getRequestURI());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "認証が必要です");
        }
    }
}