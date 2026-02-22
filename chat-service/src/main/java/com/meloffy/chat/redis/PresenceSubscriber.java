package com.meloffy.chat.redis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meloffy.chat.utils.ConnectionRegistry;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.nio.charset.StandardCharsets;
import java.util.Set;

@Component
public class PresenceSubscriber implements MessageListener {

    private final ConnectionRegistry registry;

    public PresenceSubscriber(ConnectionRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String payload = new String(message.getBody(), StandardCharsets.UTF_8);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(payload);

            if (!"PRESENCE".equals(node.get("type").asText())) return;

            String userId = node.get("userId").asText();

            // fan out to ALL connected clients
            for (Set<WebSocketSession> sessions : registry.getAllSessions()) {
                for (WebSocketSession s : sessions) {
                    if (s.isOpen()) {
                        s.sendMessage(new TextMessage(payload));
                    }
                }
            }

        } catch (Exception ignored) {}
    }
}
