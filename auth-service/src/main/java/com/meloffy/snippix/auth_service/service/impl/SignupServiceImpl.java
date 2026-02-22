package com.meloffy.snippix.auth_service.service.impl;

import com.meloffy.snippix.auth_service.dto.Loginrequest;
import com.meloffy.snippix.auth_service.dto.SignupRequest;
import com.meloffy.snippix.auth_service.dto.TokenResponse;
import com.meloffy.snippix.auth_service.grpc.UserServiceGrpcClient;
import com.meloffy.snippix.auth_service.service.SignupService;
import com.meloffy.snippix.auth_service.utils.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

@Service
public class SignupServiceImpl implements SignupService {

    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(7);

    @Value("${keycloak.admin.client-id}")
    private String clientId;

    @Value("${keycloak.admin.client-secret}")
    private String clientSecret;

    @Value("${keycloak.server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    private static final Logger log =
            LoggerFactory.getLogger(SignupServiceImpl.class);

    private final RealmResource realmResource;
    private final UserServiceGrpcClient userServiceGrpcClient;
    private final RestTemplate restTemplate;
    private final RefreshTokenRedisService refreshTokenRedisService;

    public SignupServiceImpl(RealmResource realmResource, UserServiceGrpcClient userServiceGrpcClient, RestTemplate restTemplate, RefreshTokenRedisService refreshTokenRedisService) {
        this.realmResource = realmResource;
        this.userServiceGrpcClient = userServiceGrpcClient;
        this.restTemplate = restTemplate;
        this.refreshTokenRedisService = refreshTokenRedisService;
    }

    @Override
    public TokenResponse signup(SignupRequest request) {
        if (request.getPassword() == null ||
                (request.getEmail() == null && request.getPhone() == null)) {
            throw new IllegalArgumentException("Invalid signup data");
        }

        String userId = UUID.randomUUID().toString();
        String username = request.getEmail() != null ? request.getEmail().substring(0, request.getEmail().indexOf("@")) : request.getPhone();

        UserRepresentation user = new UserRepresentation();
        user.setId(userId);
        user.setEnabled(true);
        user.setEmail(request.getEmail());
        user.setUsername(username);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setAttributes(Collections.singletonMap(
                "phone", Collections.singletonList(request.getPhone())
        ));

        Response response = realmResource.users().create(user);

        int status = response.getStatus();
        String location = response.getHeaderString("Location");

        log.info("Keycloak create user response status: {}", status);
        log.info("Keycloak create user location: {}", location);

        String createdUserId = location.substring(location.lastIndexOf("/") + 1);
        log.info("Keycloak user created with id: {}", createdUserId);

        if (status != 201) {
            String error = response.readEntity(String.class);
            log.error("Keycloak user creation failed: {}", error);
            throw new RuntimeException("Failed to create user in Keycloak");
        } else if (status == 201) {
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setTemporary(false);
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(request.getPassword());

            realmResource.users()
                    .get(createdUserId)
                    .resetPassword(credential);
        }

        userServiceGrpcClient.createUser(
                createdUserId,
                username,
                request.getFirstName() + " " + request.getLastName(),
                request.getEmail(),
                request.getPhone()
        );
        return generateToken(username, request.getPassword());
    }

    private TokenResponse generateToken(String username, String password) {

        String tokenUrl =
                keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("username", username);
        body.add("password", password);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);
        ResponseEntity<TokenResponse> response =
                restTemplate.postForEntity(tokenUrl, request, TokenResponse.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Failed to generate token");
        }

        TokenResponse resp = response.getBody();
        String keycloakUserId = JwtUtil.extractSub(resp.getAccessToken());
        refreshTokenRedisService.save(
                resp.getRefreshToken(),
                keycloakUserId,
                REFRESH_TOKEN_TTL
        );
        userServiceGrpcClient.updateLastActivity(keycloakUserId);
        log.info("login successful with username: {}", username);

        return resp;
    }

    @Override
    public TokenResponse login(Loginrequest loginrequest) {
        log.info("login requested by username: {}",  loginrequest.getUsername());
        TokenResponse response = generateToken(loginrequest.getUsername(), loginrequest.getPassword());
        if (response != null && response.getAccessToken() != null) {
            String keycloakUserId = JwtUtil.extractSub(response.getAccessToken());
//            refreshTokenRedisService.save(keycloakUserId, response.getRefreshToken(),  REFRESH_TOKEN_TTL);
            userServiceGrpcClient.updateLastActivity(keycloakUserId);
        }
        return response;
    }

    @Override
    public TokenResponse refreshAccessToken(HttpServletRequest request) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            throw new RuntimeException("Refresh token missing. Login again.");
        }
        String userId = refreshTokenRedisService.get(refreshToken);
        TokenResponse newTokens = generateTokenFromRefreshToken(userId, refreshToken);
        refreshTokenRedisService.rotate(
                refreshToken,
                newTokens.getRefreshToken(),
                userId,
                REFRESH_TOKEN_TTL
        );
        return newTokens;
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if ("refresh_token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    @Override
    public void logout(HttpServletRequest request) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        if (refreshToken != null) {
            refreshTokenRedisService.delete(refreshToken);
        }
    }

    private TokenResponse generateTokenFromRefreshToken(String userId, String refreshToken) {

        String tokenUrl =
                keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request =
                new HttpEntity<>(body, headers);

        try {
            ResponseEntity<TokenResponse> response =
                    restTemplate.postForEntity(tokenUrl, request, TokenResponse.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Failed to refresh access token");
            }

            return response.getBody();

        } catch (Exception ex) {
            refreshTokenRedisService.delete(userId);
            log.error("Refresh token exchange failed for user {}", userId, ex);
            throw new RuntimeException("Refresh token expired or invalid. Login again.");
        }
    }

}
