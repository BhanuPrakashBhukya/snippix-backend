package com.meloffy.chat.utils;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    // 🔒 Manual decoder (NOT a Spring bean)
    private final NimbusJwtDecoder jwtDecoder =
            NimbusJwtDecoder.withJwkSetUri(
                    "http://keycloak:8080/realms/snippix-realm/protocol/openid-connect/certs"
            ).build();

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {

        URI uri = request.getURI();
        String query = uri.getQuery();

        if (query == null || !query.contains("token=")) {
            return false;
        }

        String token = extractToken(query);

        try {
            Jwt jwt = jwtDecoder.decode(token);

            // ✅ Attach user identity to WebSocket session
            attributes.put("userId", jwt.getSubject());
            attributes.put("username", jwt.getClaimAsString("preferred_username"));

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private String extractToken(String query) {
        for (String param : query.split("&")) {
            if (param.startsWith("token=")) {
                return param.substring(6);
            }
        }
        return null;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // no-op
    }
}