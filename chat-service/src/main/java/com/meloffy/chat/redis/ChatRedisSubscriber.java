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

@Component
public class ChatRedisSubscriber implements MessageListener {

    private final ConnectionRegistry connectionRegistry;

    public ChatRedisSubscriber(ConnectionRegistry connectionRegistry) {
        this.connectionRegistry = connectionRegistry;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {

        String payload = new String(message.getBody(), StandardCharsets.UTF_8);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(payload);

            // === 🔒 SAFETY CHECKS ===
            if (!root.has("type") || !root.has("payload")) {
                System.err.println("BAD REDIS MESSAGE: " + payload);
                return;
            }

            JsonNode body = root.get("payload");

            if (!body.has("to")) {
                System.err.println("MISSING 'to' FIELD: " + payload);
                return;
            }

            String toUser = body.get("to").asText();

            // 🔥 Fan-out to all active sessions
            for (WebSocketSession session :
                    connectionRegistry.getSessions(toUser)) {

                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(payload));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
