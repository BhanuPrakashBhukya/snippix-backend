package com.meloffy.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GatewayFallbackController {

    @GetMapping("/fallback/user-service")
    public ResponseEntity<Map<String, String>> userServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "USER_SERVICE_UNAVAILABLE",
                        "message", "User service is temporarily unavailable"
                ));
    }

    @GetMapping("/fallback/auth-service")
    public ResponseEntity<Map<String, String>> authServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "AUTH_SERVICE_UNAVAILABLE",
                        "message", "Authentication service is temporarily unavailable"
                ));
    }

    @GetMapping("/fallback/chat-service")
    public ResponseEntity<Map<String, String>> chatServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "CHAT_SERVICE_UNAVAILABLE",
                        "message", "Chat service is temporarily unavailable"
                ));
    }
}