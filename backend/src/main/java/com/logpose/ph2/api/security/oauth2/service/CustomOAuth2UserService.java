package com.logpose.ph2.api.security.oauth2.service;

import com.logpose.ph2.api.security.exception.OAuth2AuthenticationProcessingException;
import com.logpose.ph2.api.security.user.OAuth2UserInfo;
import com.logpose.ph2.api.security.user.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequestEntityConverter;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private static Logger LOG = LogManager.getLogger(CustomOAuth2UserService.class);

    private static final String MISSING_USER_INFO_URI_ERROR_CODE = "missing_user_info_uri";
    private static final String MISSING_USER_NAME_ATTRIBUTE_ERROR_CODE = "missing_user_name_attribute";
    private static final String INVALID_USER_INFO_RESPONSE_ERROR_CODE = "invalid_user_info_response";

    private Converter<OAuth2UserRequest, RequestEntity<?>> requestEntityConverter = new OAuth2UserRequestEntityConverter();
    private static final ParameterizedTypeReference<Map<String, Object>> PARAMETERIZED_RESPONSE_TYPE = new ParameterizedTypeReference<Map<String, Object>>() {};
    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest){
        LOG.info("loadUser");
        LOG.info("getAccessToken:"+oAuth2UserRequest.getAccessToken().getTokenValue());
        LOG.info("getClientRegistration:"+oAuth2UserRequest.getClientRegistration().getRegistrationId());
        LOG.info("getProviderDetails getUserInfoEndpoint getUri:"+oAuth2UserRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri());
        LOG.info("getProviderDetails getUserInfoEndpoint getUserNameAttributeName:"+oAuth2UserRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
                .getUserNameAttributeName());

        if (!StringUtils
                .hasText(oAuth2UserRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri())) {
            OAuth2Error oauth2Error = new OAuth2Error(MISSING_USER_INFO_URI_ERROR_CODE,
                    "Missing required UserInfo Uri in UserInfoEndpoint for Client Registration: "
                            + oAuth2UserRequest.getClientRegistration().getRegistrationId(),
                    null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        String userNameAttributeName = oAuth2UserRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
                .getUserNameAttributeName();
        if (!StringUtils.hasText(userNameAttributeName)) {
            OAuth2Error oauth2Error = new OAuth2Error(MISSING_USER_NAME_ATTRIBUTE_ERROR_CODE,
                    "Missing required \"user name\" attribute name in UserInfoEndpoint for Client Registration: "
                            + oAuth2UserRequest.getClientRegistration().getRegistrationId(),
                    null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        LOG.info("CHECK Finish");

        try {
            String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
            if(registrationId.equals("heroku")){
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getInterceptors().add((request, body, execution) -> {
                    request.getHeaders().setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                    request.getHeaders().add("Content-Type", "application/json");
                    request.getHeaders().add("Accept", "application/vnd.heroku+json; version=3");
                    return execution.execute(request, body);
                });

                if (!StringUtils.hasText(userNameAttributeName)) {
                    OAuth2Error oauth2Error = new OAuth2Error(MISSING_USER_NAME_ATTRIBUTE_ERROR_CODE,
                            "Missing required \"user name\" attribute name in UserInfoEndpoint for Client Registration: "
                                    + oAuth2UserRequest.getClientRegistration().getRegistrationId(),
                            null);
                    throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
                }

                RequestEntity<?> request = requestEntityConverter.convert(oAuth2UserRequest);
                ResponseEntity<Map<String, Object>> response = restTemplate.exchange(request, PARAMETERIZED_RESPONSE_TYPE);
                Map<String, Object> userAttributes = response.getBody();
                Set<GrantedAuthority> authorities = new LinkedHashSet<>();
                authorities.add(new OAuth2UserAuthority(userAttributes));
                OAuth2AccessToken token = oAuth2UserRequest.getAccessToken();
                for (String authority : token.getScopes()) {
                    authorities.add(new SimpleGrantedAuthority("SCOPE_" + authority));
                }
                OAuth2User oAuth2User = new DefaultOAuth2User(authorities, userAttributes, userNameAttributeName);
                return processOAuth2User(oAuth2UserRequest, oAuth2User);
            }else {
                OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
                return processOAuth2User(oAuth2UserRequest, oAuth2User);
            }
        } catch (AuthenticationException ex) {
            ex.printStackTrace();
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            ex.printStackTrace();
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        LOG.info("processOAuth2User");

        String registrationId = userRequest.getClientRegistration()
                .getRegistrationId();

        String accessToken = userRequest.getAccessToken().getTokenValue();

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId,
                accessToken,
                oAuth2User.getAttributes());

        // OAuth2UserInfo field value validation
        if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        return new OAuth2UserPrincipal(oAuth2UserInfo);
    }

//    private ResponseEntity<Map<String, Object>> getResponse(OAuth2UserRequest userRequest, RequestEntity<?> request) {
//        try {
//            return this.restOperations.exchange(request, PARAMETERIZED_RESPONSE_TYPE);
//        }
//        catch (OAuth2AuthorizationException ex) {
//            OAuth2Error oauth2Error = ex.getError();
//            StringBuilder errorDetails = new StringBuilder();
//            errorDetails.append("Error details: [");
//            errorDetails.append("UserInfo Uri: ")
//                    .append(userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri());
//            errorDetails.append(", Error Code: ").append(oauth2Error.getErrorCode());
//            if (oauth2Error.getDescription() != null) {
//                errorDetails.append(", Error Description: ").append(oauth2Error.getDescription());
//            }
//            errorDetails.append("]");
//            oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE,
//                    "An error occurred while attempting to retrieve the UserInfo Resource: " + errorDetails.toString(),
//                    null);
//            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex);
//        }
//        catch (UnknownContentTypeException ex) {
//            String errorMessage = "An error occurred while attempting to retrieve the UserInfo Resource from '"
//                    + userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri()
//                    + "': response contains invalid content type '" + ex.getContentType().toString() + "'. "
//                    + "The UserInfo Response should return a JSON object (content type 'application/json') "
//                    + "that contains a collection of name and value pairs of the claims about the authenticated End-User. "
//                    + "Please ensure the UserInfo Uri in UserInfoEndpoint for Client Registration '"
//                    + userRequest.getClientRegistration().getRegistrationId() + "' conforms to the UserInfo Endpoint, "
//                    + "as defined in OpenID Connect 1.0: 'https://openid.net/specs/openid-connect-core-1_0.html#UserInfo'";
//            OAuth2Error oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE, errorMessage, null);
//            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex);
//        }
//        catch (RestClientException ex) {
//            OAuth2Error oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE,
//                    "An error occurred while attempting to retrieve the UserInfo Resource: " + ex.getMessage(), null);
//            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex);
//        }
//    }
}