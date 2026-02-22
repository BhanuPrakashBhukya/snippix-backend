package com.meloffy.chat.service;

import com.meloffy.chat.enums.MessageStatus;
import com.meloffy.chat.model.ChatMessage;
import com.meloffy.chat.repository.ChatMessageRepository;
import com.meloffy.chat.utils.ConnectionRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ChatMessageService {

    private final ChatMessageRepository repository;
    private final ConnectionRegistry connectionRegistry;

    public ChatMessageService(ChatMessageRepository repository, ConnectionRegistry connectionRegistry) {
        this.repository = repository;
        this.connectionRegistry = connectionRegistry;
    }

    public ChatMessage sendMessage(String fromUser, ChatMessage incoming) {

        String conversationId = buildConversationId(fromUser, incoming.getTo());
        UUID messageId = UUID.randomUUID();

        ChatMessage enriched = new ChatMessage();
        enriched.setMessageId(messageId.toString());
        enriched.setFrom(fromUser);
        enriched.setTo(incoming.getTo());
        enriched.setContent(incoming.getContent());
        enriched.setTimestamp(Instant.now().toEpochMilli());


        Set<WebSocketSession> receiverSessions =
                connectionRegistry.getSessions(incoming.getTo());

        if (!receiverSessions.isEmpty()) {
            enriched.setStatus(MessageStatus.DELIVERED);
        } else {
            enriched.setStatus(MessageStatus.SENT);
        }

        repository.saveMessage(conversationId, enriched);

        return enriched;
    }

    public void markRead(String conversationId, String messageTime) {
        repository.markRead(
                conversationId,
                UUID.fromString(messageTime)
        );
    }

    public String buildConversationId(String u1, String u2) {
        return (u1.compareTo(u2) < 0)
                ? u1 + "#" + u2
                : u2 + "#" + u1;
    }

    public List<ChatMessage> fetchUndelivered(String userId) {
        return repository.fetchUndelivered(userId);
    }

    public void deleteUndelivered(String userId, String messageTimeUuid) {
        repository.deleteUndelivered(userId, java.util.UUID.fromString(messageTimeUuid));
    }

    public void markDelivered(String conversationId, String messageTime) {
        repository.markDelivered(
                conversationId,
                UUID.fromString(messageTime)
        );
    }

    public List<ChatMessage> fetchConversation(String conversationId, int limit) {
        return repository.fetchConversation(conversationId, limit);
    }
}
