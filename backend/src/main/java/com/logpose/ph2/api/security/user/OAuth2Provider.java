package com.logpose.ph2.api.security.user;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuth2Provider {
    HEROKU("heroku");

    private final String registrationId;
}