package com.meloffy.snippix.auth_service.controller;

import com.meloffy.snippix.auth_service.dto.Loginrequest;
import com.meloffy.snippix.auth_service.dto.SignupRequest;
import com.meloffy.snippix.auth_service.dto.TokenResponse;
import com.meloffy.snippix.auth_service.service.SignupService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final SignupService signupService;

    public AuthController(SignupService signupService) {
        this.signupService = signupService;
    }

    @PostMapping("/signup")
    public ResponseEntity<TokenResponse> signup(
            @RequestBody SignupRequest signupRequest,
            HttpServletResponse response
    ) {
        TokenResponse tokenResponse = signupService.signup(signupRequest);

        addRefreshCookie(response, tokenResponse.getRefreshToken());

        tokenResponse.setRefreshToken(null);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @RequestBody Loginrequest loginrequest,
            HttpServletResponse response
    ) {
        TokenResponse tokenResponse = signupService.login(loginrequest);

        addRefreshCookie(response, tokenResponse.getRefreshToken());

        tokenResponse.setRefreshToken(null);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        TokenResponse tokenResponse = signupService.refreshAccessToken(request);

        // optionally rotate refresh token
        addRefreshCookie(response, tokenResponse.getRefreshToken());

        tokenResponse.setRefreshToken(null);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {

        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(false)     // true in prod
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
        return ResponseEntity.ok().build();
    }

    // ===== COOKIE HELPER =====
    private void addRefreshCookie(HttpServletResponse response, String refreshToken) {

        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false)        // ⚠️ true only if HTTPS
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Lax")     // 🔥 REQUIRED for cross-origin
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}