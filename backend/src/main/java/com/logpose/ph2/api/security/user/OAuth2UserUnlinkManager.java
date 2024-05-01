package com.logpose.ph2.api.security.user;

import com.logpose.ph2.api.security.exception.OAuth2AuthenticationProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OAuth2UserUnlinkManager {
    @Autowired
    private HerokuOAuth2UserUnlink herokuOAuth2UserUnlink;

    public void unlink(OAuth2Provider provider, String token) {
        if (OAuth2Provider.HEROKU.equals(provider)) {
            herokuOAuth2UserUnlink.unlink(token);
        } else {
            throw new OAuth2AuthenticationProcessingException(
                    "Unlink with " + provider.getRegistrationId() + " is not supported");
        }
    }
}
