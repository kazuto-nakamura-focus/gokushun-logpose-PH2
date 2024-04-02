package com.logpose.ph2.api.security.oauth2.handler;

import com.logpose.ph2.api.security.jwt.TokenProvider;
import com.logpose.ph2.api.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.logpose.ph2.api.security.oauth2.service.OAuth2UserPrincipal;
import com.logpose.ph2.api.security.oauth2.util.CookieUtils;
import com.logpose.ph2.api.security.user.OAuth2Provider;
import com.logpose.ph2.api.security.user.OAuth2UserUnlinkManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

import static com.logpose.ph2.api.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.MODE_PARAM_COOKIE_NAME;
import static com.logpose.ph2.api.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;
@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static Logger LOG = LogManager.getLogger(OAuth2AuthenticationSuccessHandler.class);
    @Value("${spring.security.oauth2.client.registration.heroku.redirect-uri}")
    private String frontendRedirectUri;

    @Autowired
    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOauth2AuthorizationRequestRepository;

    @Autowired
    private OAuth2UserUnlinkManager oAuth2UserUnlinkManager;
    @Autowired
    private TokenProvider tokenProvider;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        LOG.info("onAuthenticationSuccess");
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            LOG.debug("Response has already been committed. " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication){
        LOG.info("determineTargetUrl");
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        String mode = CookieUtils.getCookie(request, MODE_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse("");

        OAuth2UserPrincipal principal = getOAuth2UserPrincipal(authentication);
        LOG.info("determineTargetUrl:{}, {}, {}, ", targetUrl, mode, principal);

        if (principal == null) {
            return UriComponentsBuilder.fromUriString(targetUrl)
                    .queryParam("error", "Login failed")
                    .build().toUriString();
        }

        if ("login".equalsIgnoreCase(mode)) {
            // TODO: DB 保存
            // TODO: アクセストークン、リフレッシュトークン発行
            // TODO: リフレッシュトークンをDBに保存
            LOG.info("email={}, name={}, nickname={}, accessToken={}", principal.getUserInfo().getEmail(),
                    principal.getUserInfo().getName(),
                    principal.getUserInfo().getNickname(),
                    principal.getUserInfo().getAccessToken()
            );

            String accessToken = tokenProvider.createToken(authentication);
            String refreshToken = "refresh_token";

            return UriComponentsBuilder.fromUriString(targetUrl)
//                    .queryParam("access_token", accessToken)
//                    .queryParam("refresh_token", refreshToken)
                    .build().toUriString();

        } else if ("unlink".equalsIgnoreCase(mode)) {
            String accessToken = principal.getUserInfo().getAccessToken();
            OAuth2Provider provider = principal.getUserInfo().getProvider();
            LOG.info("accessToken:"+accessToken);

            // TODO: DB 削除
            // TODO: リフレッシュトークン削除
            oAuth2UserUnlinkManager.unlink(provider, accessToken);

            return UriComponentsBuilder.fromUriString("/api/logout")
                    .build().toUriString();
        }

        return UriComponentsBuilder.fromUriString(frontendRedirectUri)
//                .queryParam("error", "Login failed")
                .build().toUriString();
    }

    private OAuth2UserPrincipal getOAuth2UserPrincipal(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof OAuth2UserPrincipal) {
            LOG.info("principal type:"+principal.getClass().getSimpleName());
            return (OAuth2UserPrincipal) principal;
        }
        return null;
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOauth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}
