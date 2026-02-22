package com.meloffy.chat.repository;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.meloffy.chat.enums.MessageStatus;
import com.meloffy.chat.model.ChatMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ChatMessageRepository {

    private final CqlSession session;

    public ChatMessageRepository(CqlSession session) {
        this.session = session;
    }

    public void saveMessage(String conversationId, ChatMessage msg) {

        UUID timeUuid = Uuids.timeBased();

        session.execute(
                """
                INSERT INTO messages_by_conversation
                (conversation_id, message_time, message_id,
                 sender_id, receiver_id, content, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                conversationId,
                timeUuid,
                UUID.fromString(msg.getMessageId()),
                msg.getFrom(),
                msg.getTo(),
                msg.getContent(),
                msg.getStatus().name()
        );

        if (msg.getStatus() == MessageStatus.SENT) {
            session.execute(
                    """
                    INSERT INTO undelivered_messages_by_user
                    (receiver_id, message_time, conversation_id,
                     message_id, sender_id, content)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """,
                    msg.getTo(),
                    timeUuid,
                    conversationId,
                    UUID.fromString(msg.getMessageId()),
                    msg.getFrom(),
                    msg.getContent()
            );
        }
    }

    public void markRead(String conversationId, UUID messageTime) {
        session.execute(
                """
                UPDATE messages_by_conversation
                SET status = ?
                WHERE conversation_id = ? AND message_time = ?
                """,
                MessageStatus.READ.name(),
                conversationId,
                messageTime
        );
    }

    public List<ChatMessage> fetchUndelivered(String receiverId) {

        List<ChatMessage> result = new ArrayList<>();

        for (Row row : session.execute(
                """
                SELECT receiver_id, message_time, conversation_id,
                       message_id, sender_id, content
                FROM undelivered_messages_by_user
                WHERE receiver_id = ?
                """,
                receiverId
        )) {

            ChatMessage msg = new ChatMessage();

            UUID messageTime = row.getUuid("message_time"); // ✅ TIMEUUID

            msg.setMessageId(row.getUuid("message_id").toString());
            msg.setMessageTime(messageTime.toString());     // 🔥 FIX
            msg.setFrom(row.getString("sender_id"));
            msg.setTo(receiverId);
            msg.setContent(row.getString("content"));
            msg.setStatus(MessageStatus.DELIVERED);
            msg.setTimestamp(messageTime.timestamp());

            result.add(msg);
        }

        return result;
    }

    public void deleteUndelivered(String receiverId, UUID messageTime) {
        session.execute(
                """
                DELETE FROM undelivered_messages_by_user
                WHERE receiver_id = ? AND message_time = ?
                """,
                receiverId,
                messageTime
        );
    }

    public void markDelivered(String conversationId, UUID messageTime) {
        session.execute(
                """
                UPDATE messages_by_conversation
                SET status = ?
                WHERE conversation_id = ? AND message_time = ?
                """,
                MessageStatus.DELIVERED.name(),
                conversationId,
                messageTime
        );
    }

    public List<ChatMessage> fetchConversation(String conversationId, int limit) {

        List<ChatMessage> result = new ArrayList<>();

        for (Row row : session.execute(
                """
                SELECT conversation_id, message_time, message_id,
                       sender_id, receiver_id, content, status
                FROM messages_by_conversation
                WHERE conversation_id = ?
                LIMIT ?
                """,
                conversationId,
                limit
        )) {

            ChatMessage msg = new ChatMessage();
            UUID messageTime = row.getUuid("message_time"); // TIMEUUID

            msg.setMessageId(row.getUuid("message_id").toString());
            msg.setMessageTime(messageTime.toString());
            msg.setFrom(row.getString("sender_id"));
            msg.setTo(row.getString("receiver_id"));
            msg.setContent(row.getString("content"));
            msg.setStatus(MessageStatus.valueOf(row.getString("status")));
            msg.setTimestamp(messageTime.timestamp());

            result.add(msg);
        }
        return result;
    }
}
