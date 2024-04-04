package com.logpose.ph2.api.security.user;

import com.logpose.ph2.api.security.exception.OAuth2AuthenticationProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class OAuth2UserInfoFactory {
    private static Logger LOG = LogManager.getLogger(OAuth2UserInfoFactory.class);
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId,
                                                   String accessToken,
                                                   Map<String, Object> attributes) {
        LOG.info("getOAuth2UserInfo:{}", accessToken);

        if (OAuth2Provider.HEROKU.getRegistrationId().equals(registrationId)) {
            return new HerokuOAuth2UserInfo(accessToken, attributes);
        } else {
            throw new OAuth2AuthenticationProcessingException("Login with " + registrationId + " is not supported");
        }
    }
}
