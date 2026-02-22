package com.meloffy.chat.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.meloffy.chat.dto.WsEnvelope;
import com.meloffy.chat.model.ChatMessage;
import com.meloffy.chat.redis.ChatRedisPublisher;
import com.meloffy.chat.redis.PresencePublisher;
import com.meloffy.chat.service.ChatMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log =
            LoggerFactory.getLogger(ChatWebSocketHandler.class);

    private final ConnectionRegistry sessionRegistry;
    private final ChatMessageService chatMessageService;
    private final ChatRedisPublisher chatRedisPublisher;
    private final PresencePublisher publishPresence;

    public ChatWebSocketHandler(ConnectionRegistry sessionRegistry, ChatMessageService chatMessageService, ChatRedisPublisher chatRedisPublisher, PresencePublisher publishPresence) {
        this.sessionRegistry = sessionRegistry;
        this.chatMessageService = chatMessageService;
        this.chatRedisPublisher = chatRedisPublisher;
        this.publishPresence = publishPresence;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        String userId = (String) session.getAttributes().get("userId");

        // Safety check (handshake interceptor should guarantee this)
        if (userId == null) {
            log.warn(
                    "WS REJECTED | sessionId={} | reason=missing userId",
                    session.getId()
            );
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }
        // 1️⃣ Bind user to session (presence)
        boolean firstSession = sessionRegistry.bindUser(userId, session);
        if (firstSession) {
            publishPresence.publish(userId, "ONLINE");
        }
        log.info(
                "WS CONNECTED | userId={} | sessionId={} | remote={}",
                userId,
                session.getId(),
                session.getRemoteAddress()
        );

        ObjectMapper mapper = new ObjectMapper();

        for (String onlineUserId : sessionRegistry.getOnlineUserIds()) {

            ObjectNode presence = mapper.createObjectNode();
            presence.put("type", "PRESENCE");
            presence.put("userId", onlineUserId);
            presence.put("status", "ONLINE");

            session.sendMessage(
                    new TextMessage(presence.toString())
            );
        }

        // 2️⃣ Fetch undelivered messages from Cassandra
        List<ChatMessage> pending =
                chatMessageService.fetchUndelivered(userId);

        if (pending.isEmpty()) {
            return;
        }

        ObjectMapper mapper1 = new ObjectMapper();

        // 3️⃣ Send each pending message to the client
        for (ChatMessage msg : pending) {

            ObjectNode outbound = mapper1.createObjectNode();
            outbound.put("messageId", msg.getMessageId());
            outbound.put("from", msg.getFrom());
            outbound.put("to", msg.getTo());
            outbound.put("content", msg.getContent());
            outbound.put("status", msg.getStatus().name());
            outbound.put("timestamp", msg.getTimestamp());

            session.sendMessage(
                    new TextMessage(outbound.toString())
            );

            // 4️⃣ Remove from undelivered table
            // IMPORTANT: delete using message_time (TIMEUUID)
            chatMessageService.deleteUndelivered(
                    userId,
                    msg.getMessageTime()   // ← TIMEUUID as String
            );

            // 2️⃣ mark DELIVERED in conversation table
            chatMessageService.markDelivered(
                    buildConversationId(msg.getFrom(), userId),
                    msg.getMessageTime()
            );
        }

        log.info(
                "WS OFFLINE DELIVERY | userId={} | deliveredCount={}",
                userId,
                pending.size()
        );
    }

    private String buildConversationId(String u1, String u2) {
        return (u1.compareTo(u2) < 0)
                ? u1 + "#" + u2
                : u2 + "#" + u1;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message)
            throws Exception {

        String fromUserId = (String) session.getAttributes().get("userId");
        String text = message.getPayload();

        log.info("WS MESSAGE | fromUser={} | payload={}", fromUserId, text);

        ObjectMapper mapper = new ObjectMapper();
        WsEnvelope env = mapper.readValue(text, WsEnvelope.class);

        switch (env.getType().toUpperCase()) {

            case "PING" -> {
                session.sendMessage(new TextMessage("{\"type\":\"PONG\"}"));
            }

            case "READ" -> {
                handleRead(env.getPayload());
            }

            case "CHAT" -> {
                handleChat(fromUserId, env.getPayload(), session);
            }

            case "LOGOUT" -> {
                handleLogout(session);
            }

            default -> log.warn("WS UNKNOWN TYPE | userId={} | type={}",
                    fromUserId, env.getType());
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {

        String userId = (String) session.getAttributes().get("userId");

        log.error(
                "WS ERROR | userId={} | sessionId={} | error={}",
                userId,
                session.getId(),
                exception.getMessage(),
                exception
        );
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {

        String userId = (String) session.getAttributes().get("userId");

        if (userId != null) {
            boolean becameOffline = sessionRegistry.unbindUser(userId, session);
            if (becameOffline) {
                publishPresence.publish(userId, "OFFLINE");
            }
        }

        log.info(
                "WS DISCONNECTED | userId={} | sessionId={} | status={}",
                userId,
                session.getId(),
                status
        );
    }

    private void handleChat(String fromUserId,
                            JsonNode payload,
                            WebSocketSession session) throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        // Read incoming message
        ChatMessage incoming =
                mapper.treeToValue(payload, ChatMessage.class);

        // 👇 Capture clientTempId if client sent it
        String clientTempId = null;
        if (payload.has("clientTempId")) {
            clientTempId = payload.get("clientTempId").asText();
        }

        // Persist message
        ChatMessage persisted =
                chatMessageService.sendMessage(fromUserId, incoming);

        // ======= BUILD OUTBOUND CHAT EVENT =======
        ObjectNode outbound = mapper.createObjectNode();
        outbound.put("type", "CHAT");

        // Convert persisted message to JSON node
        ObjectNode serverPayload =
                mapper.valueToTree(persisted);

        // 🔥 CRITICAL LINE — echo back tempId
        if (clientTempId != null) {
            serverPayload.put("clientTempId", clientTempId);
        }

        outbound.set("payload", serverPayload);

        String json = outbound.toString();

        // Deliver to receiver
        Set<WebSocketSession> targets =
                sessionRegistry.getSessions(persisted.getTo());

        boolean delivered = false;

        if (!targets.isEmpty()) {
            for (WebSocketSession target : targets) {
                if (target.isOpen()) {
                    target.sendMessage(new TextMessage(json));
                    delivered = true;
                }
            }
        } else {
            chatRedisPublisher.publishToUser(
                    persisted.getTo(),
                    json
            );
        }

        // ======= SEND DELIVERED RECEIPT BACK TO SENDER =======
        if (delivered) {
            ObjectNode deliveredReceipt = mapper.createObjectNode();
            deliveredReceipt.put("type", "DELIVERED");
            deliveredReceipt.put("messageId", persisted.getMessageId());
            deliveredReceipt.put(
                    "conversationId",
                    buildConversationId(fromUserId, persisted.getTo())
            );
            deliveredReceipt.put("clientTempId", clientTempId);

            String deliveredJson = deliveredReceipt.toString();

            for (WebSocketSession s : sessionRegistry.getSessions(fromUserId)) {
                if (s.isOpen()) {
                    s.sendMessage(new TextMessage(deliveredJson));
                }
            }
        }
    }

    private void handleRead(JsonNode payload) throws IOException {

        String conversationId = payload.get("conversationId").asText();
        String messageTime = payload.get("messageTime").asText();
        String sender = payload.get("sender").asText();

        chatMessageService.markRead(conversationId, messageTime);

        ObjectNode receipt = new ObjectMapper().createObjectNode();
        receipt.put("type", "READ");
        receipt.put("messageTime", messageTime);

        for (WebSocketSession s : sessionRegistry.getSessions(sender)) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(receipt.toString()));
            }
        }

        log.info("WS READ | conversationId={} | messageTime={}",
                conversationId, messageTime);
    }

    private void handleLogout(WebSocketSession session) throws Exception {

        String userId = (String) session.getAttributes().get("userId");
        if (userId == null) return;
//        sessionRegistry.removeUser(userId);
        boolean becameOffline =
                sessionRegistry.unbindUser(userId, session);

        if (becameOffline) {
            publishPresence.publish(userId, "OFFLINE");
        }
        session.close(CloseStatus.NORMAL);
    }

}