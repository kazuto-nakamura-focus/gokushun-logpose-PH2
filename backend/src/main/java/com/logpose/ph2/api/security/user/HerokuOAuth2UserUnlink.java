package com.logpose.ph2.api.security.user;

import com.logpose.ph2.api.controller.DeviceDataLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class HerokuOAuth2UserUnlink implements OAuth2UserUnlink {
    private static Logger LOG = LogManager.getLogger(HerokuOAuth2UserUnlink.class);
    @Value("${spring.security.oauth2.client.registration.heroku.client-id}")
    private String clientId;
    @Value(("${spring.security.oauth2.client.provider.heroku.authorization-uri}"))
    private String authorizationUri;


    private String URL = "https://api.heroku.com/oauth/authorizations/";

    @Override
    public void unlink(String accessToken) {
        try {
            String bearerValue = String.format("Bearer %s", accessToken);
            LOG.info("unlink:{}, {}", accessToken, bearerValue);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "application/vnd.heroku+json; version=3");
//            headers.setBearerAuth(accessToken);
            headers.set("Authorization", bearerValue);

            RestTemplate restTemplate = new RestTemplate();

            String unlinkUrl = String.format("%s%s", URL, clientId);

            HttpEntity<String> entity = new HttpEntity<>(headers);
            LOG.info("Unlink:{}", unlinkUrl);
//            ResponseEntity<String> response = restTemplate.exchange(
//                    unlinkUrl,
//                    HttpMethod.DELETE,
//                    entity,
//                    String.class
//            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}