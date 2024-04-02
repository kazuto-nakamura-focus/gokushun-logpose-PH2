package com.logpose.ph2.api.controller;

import com.logpose.ph2.api.dto.FieldInfoDTO;
import com.logpose.ph2.api.dto.ResponseDTO;
import com.logpose.ph2.api.security.oauth2.service.OAuth2UserPrincipal;
import com.logpose.ph2.api.security.user.OAuth2UserInfo;
import com.logpose.ph2.api.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;


/**
 * ログイン・ログアウトのコールバック
 *
 * @version 1.0
 * @since 2024/01/03
 */
@CrossOrigin(
        origins = {"http://localhost:8080", "http://localhost:3000", "https://gokushun-ph2-it.herokuapp.com"},
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
        allowedHeaders = "*", exposedHeaders = "*",
        allowCredentials = "true")
@RestController
@RequestMapping(path = "/api/auth")
public class AuthController {
    // ===============================================
    // クラスメンバー
    // ===============================================
    private static Logger LOG = LogManager.getLogger(AuthController.class);

    @Value("${spring.security.oauth2.client.registration.heroku.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.heroku.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.heroku.redirect-uri}")
    private String redirectUri;

    @Value("${spring.security.oauth2.client.provider.heroku.token-uri}")
    private String tokenUri;

    @Value(("${spring.security.oauth2.client.provider.heroku.authorization-uri}"))
    private String authorizationUri;

    @Autowired
    private AuthService authService;


    // ===============================================
    // パブリック関数群
    // ===============================================
    // --------------------------------------------------

    /**
     * herokuからのコールバックの受付
     *
     * @throws IOException
     */
    // --------------------------------------------------
    @GetMapping("/callback")
    public ResponseEntity handleCallback(@RequestParam("code") String code) throws IOException {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("grant_type", "authorization_code");
            map.add("client_id", clientId);
            map.add("client_secret", clientSecret);
            map.add("code", code);
            map.add("redirect_uri", redirectUri);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(tokenUri, request, String.class);

            LOG.info(response.toString());

//            OAuth2AuthenticationToken authToken = new OAuth2AuthenticationToken(code);
//            LOG.info("authToken:" + authToken.toString());
//
//            Authentication authentication = this.authenticationManager.authenticate(authToken);
//            LOG.info("authentication:" + authentication.toString());

            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", redirectUri)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
//    // --------------------------------------------------
//
//    /**
//     * herokuからのログアウト
//     *
//     * @throws IOException
//     */
//    // --------------------------------------------------
//    @GetMapping("/logout")
//    public ResponseDTO logout(HttpServletRequest request) throws IOException, ServletException {
//        request.logout();
//        HttpSession session = request.getSession(true);
//        session.invalidate();
//        ResponseDTO dto = new ResponseDTO();
//        dto.setSuccess(null);
//        return dto;

//        String redirectUrl = String.format("%s?client_id=%s&response_type=code&redirect_uri=%s", authorizationUri, clientId, redirectUri);

//        return ResponseEntity.status(HttpStatus.FOUND)
//                .header("Location", redirectUrl)
//                .build();
//    }

    @GetMapping("/session/check")
    public boolean checkSession(@AuthenticationPrincipal UserDetails userDetails) {
        boolean result = (userDetails != null);
        return result;
    }

    @GetMapping("/user")
    public ResponseDTO getUser(@AuthenticationPrincipal UserDetails userDetails){
        ResponseDTO dto = new ResponseDTO();
        try {
            if(userDetails instanceof OAuth2UserPrincipal){
                OAuth2UserPrincipal principal = (OAuth2UserPrincipal)userDetails;
                OAuth2UserInfo userInfo = principal.getUserInfo();
                dto.setSuccess(userInfo);
            }
        }catch(Exception e) {
            dto.setError(e);
        }
        return dto;
    }
}
