package com.meloffy.snippix.auth_service.service;

import com.meloffy.snippix.auth_service.dto.Loginrequest;
import com.meloffy.snippix.auth_service.dto.SignupRequest;
import com.meloffy.snippix.auth_service.dto.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface SignupService {
    TokenResponse signup(SignupRequest signupRequest);

    TokenResponse login(Loginrequest loginrequest);

    TokenResponse refreshAccessToken(HttpServletRequest request);

    void logout(HttpServletRequest substring);
}
