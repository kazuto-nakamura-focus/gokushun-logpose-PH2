package com.logpose.ph2.api.security.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.logpose.ph2.api.security.oauth2.service.OAuth2UserPrincipal;
import com.logpose.ph2.api.security.user.OAuth2UserInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Date;


//@RequiredArgsConstructor
//@Component
public class TokenProvider {
    private static Logger LOG = LogManager.getLogger(TokenProvider.class);

    public static final String TOKEN_COOKIE_NAME = "TOKEN";

    private static final long ACCESS_TOKEN_EXPIRE_TIME_IN_MILLISECONDS = 60 * 60 * 24 * 30; // 30日
    @Value("${jwt.secret}")
    private String secret;
    private Key key;

//    @Autowired
    private ObjectMapper objectMapper;

//    @PostConstruct
    public void init() throws NoSuchAlgorithmException {
        LOG.info("init:" + secret);
        byte[] key = Decoders.BASE64URL.decode(secret);
        this.key = Keys.hmacShaKeyFor(key);
    }

    public boolean validateToken(String token) {
        LOG.info("validateToken:" + token);

        try {
            Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (UnsupportedJwtException | MalformedJwtException exception) {
            LOG.error("JWT is not valid");
        } catch (SignatureException exception) {
            LOG.error("JWT signature validation fails");
        } catch (ExpiredJwtException exception) {
            LOG.error("JWT is expired");
        } catch (IllegalArgumentException exception) {
            LOG.error("JWT is null or empty or only whitespace");
        } catch (Exception exception) {
            LOG.error("JWT validation fails", exception);
        }

        return false;
    }

    public String createToken(Authentication authentication) {
        try {
            LOG.info("createToken:" + authentication);
            Object principal = authentication.getPrincipal();
            OAuth2UserPrincipal oAuth2UserPrincipal = null;
            OAuth2UserInfo userInfo = null;
            if (principal instanceof OAuth2UserPrincipal) {
                oAuth2UserPrincipal = (OAuth2UserPrincipal) principal;
                userInfo = oAuth2UserPrincipal.getUserInfo();
            }

            String userInfoJson = objectMapper.writeValueAsString(principal);

            Date date = new Date();
            Date expiryDate = new Date(date.getTime() + ACCESS_TOKEN_EXPIRE_TIME_IN_MILLISECONDS);

            return Jwts.builder()
                .setSubject(authentication.getName())
//                    .claim("userInfo", userInfoJson)
                    .setIssuedAt(date)
                    .setExpiration(expiryDate)
                    .signWith(key, SignatureAlgorithm.HS512)
                    .compact();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Authentication getAuthentication(String token) throws JsonProcessingException {
        LOG.info("getAuthentication:" + token);
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

//        String userInfoJson = claims.get("userInfo", String.class);
//        OAuth2UserInfo userInfo = objectMapper.readValue(userInfoJson, OAuth2UserInfo.class);
//        OAuth2UserPrincipal principal = new OAuth2UserPrincipal(userInfo);

//        if (principalObject instanceof OAuth2UserPrincipal) {
//            OAuth2UserPrincipal principal = (OAuth2UserPrincipal)principalObject;
//            return new UsernamePasswordAuthenticationToken(principal, "", Collections.emptyList());
//        }
//        return null;
//        String principalJson = claims.get("principal", String.class);
//        OAuth2UserPrincipal principal = objectMapper.readValue(principalJson, OAuth2UserPrincipal.class);
        UserDetails user = new User(claims.getSubject(), "", Collections.emptyList());

        return new UsernamePasswordAuthenticationToken(user, "", Collections.emptyList());
//        return new UsernamePasswordAuthenticationToken(principal, "", Collections.emptyList());
    }
}