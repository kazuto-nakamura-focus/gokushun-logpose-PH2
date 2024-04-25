package com.logpose.ph2.api.security.jwt;

import com.logpose.ph2.api.security.oauth2.service.OAuth2UserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;

//@RequiredArgsConstructor
//@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private static Logger LOG = LogManager.getLogger(JwtAuthorizationFilter.class);

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
//    @Autowired
    private TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);

        if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
            LOG.info("TOKEN:" + token);
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
//        String token = request.getHeader(AUTHORIZATION_HEADER);
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                LOG.info("" + cookie.getName() + ", " + cookie.getValue());
            }
            Optional<Cookie> optionalCookie = Arrays.asList(cookies).stream().filter(cookie -> cookie.getName().equals(TokenProvider.TOKEN_COOKIE_NAME)).findFirst();
            if (optionalCookie.isPresent()) {
                Cookie tokenCookie = optionalCookie.get();
                String token = tokenCookie.getValue();
                return token;
            }
        }

//
//        LOG.info("resolveToken:" + token);
//        LOG.info("getSession:"+request.getSession(false).getId());
//        LOG.info("getSession:"+request.getSession(false).getCreationTime());
//        LOG.info("getSession:"+request.getSession(false).getAttributeNames());
//        LOG.info("getSession:"+request.getSession(false).getLastAccessedTime());
//        LOG.info("getSession:"+request.getSession(false).getMaxInactiveInterval());
//        LOG.info("getSession:"+request.getSession(false).getServletContext());
//        LOG.info("getSession:"+request.getSession(false).getLastAccessedTime());
//        LOG.info("getSession:"+request.getSession(false).getMaxInactiveInterval());
//
//        Iterator headers = request.getHeaderNames().asIterator();
//        LOG.info("--------------------------------------------");
//        while (headers.hasNext()) {
//            String headerName = (String) headers.next();
//            String headerValue = request.getHeader(headerName);
//            LOG.info("HeaderName:" + headerName + ", " + headerValue);
//        }
//        LOG.info("--------------------------------------------");

//        if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
//            return token.substring(BEARER_PREFIX.length());
//        }

        return null;
    }
}