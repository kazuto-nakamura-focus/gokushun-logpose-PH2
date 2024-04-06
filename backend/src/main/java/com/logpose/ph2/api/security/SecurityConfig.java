package com.logpose.ph2.api.security;

import java.io.IOException;
import java.util.List;

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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.logpose.ph2.api.security.jwt.JwtAuthorizationFilter;
import com.logpose.ph2.api.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.logpose.ph2.api.security.oauth2.handler.OAuth2AuthenticationFailureHandler;
import com.logpose.ph2.api.security.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import com.logpose.ph2.api.security.oauth2.service.CustomOAuth2UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.cors.CorsConfiguration;

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


//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:8080", "http://localhost:3000", "https://gokushun-ph2-it.herokuapp.com"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headersConfigurer -> headersConfigurer.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((requests) ->
                        //認証なしで接続できるURLの設定
                        requests
                                .requestMatchers("/api/auth/**", "/api/bulk/update").permitAll()
                                .anyRequest().authenticated()
                )
//                .sessionManagement(sessions -> sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //OAuth2ログイン設定
                .oauth2Login(configure ->
                                configure
                                        .authorizationEndpoint(config -> config
                                                .authorizationRequestRepository(httpCookieOauth2AuthorizationRequestRepository)
                                                //OAuth2ログインURI変更
                                                //デフォルトURL：{baseUrl}/oauth/authorization
                                                //ログインURL：{baseUrl}/api/oauth2/authorization/heroku?redirect_uri={frontendのredirectUrl}&mode=login
                                                //ログアウトURL：{baseUrl}/api/oauth2/authorization/heroku?redirect_uri={frontendのredirectUrl}&mode=unlink
                                                //※ログアウトURLに接続する場合、下記のログアウト設定で設定したURLにリダイレクトするように、customOAuth2UserService経由でoauth2AuthenticationSuccessHandlerで処理
                                                .baseUri("/api/oauth2/authorization"))
                                        .userInfoEndpoint(config -> config.userService(customOAuth2UserService))
                                        //ログイン成功時のHandler
                                        .successHandler(oauth2AuthenticationSuccessHandler)
                                        //ログイン失敗時のHandler
                                        .failureHandler(oauth2AuthenticationFailureHandler)
                                        //OAuth2プロバイダーの認証後にコールバックするURLを変更
                                        //デフォルト：/login/oauth2/code/*
                                        .loginProcessingUrl("/api/login/oauth2/code/*")
                )
                //ログアウト設定
                .logout(configure ->
                        configure
                                //フロントエンドとバックエンドのセッションを切るURLを変更
                                .logoutRequestMatcher(new AntPathRequestMatcher("/api/logout"))
                                .deleteCookies("JSESSIONID")
                                .invalidateHttpSession(true)
                                .logoutSuccessUrl(redirectUri)
                                .clearAuthentication(true)  // 認証情報をクリア
                );
//                .exceptionHandling(exceptionHandling ->
//                        exceptionHandling.authenticationEntryPoint(new Http401AuthenticationEntryPoint())
//                );

        http.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    public class Http401AuthenticationEntryPoint implements AuthenticationEntryPoint {

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "認証が必要です");
        }
    }
}