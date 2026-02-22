package com.meloffy.snippix.auth_service.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;
import java.util.Map;

public class JwtUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String extractSub(String accessToken) {
        try {
            String[] parts = accessToken.split("\\.");
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            Map<String, Object> claims = mapper.readValue(payload, Map.class);
            return (String) claims.get("sub");
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract sub from token", e);
        }
    }


}
