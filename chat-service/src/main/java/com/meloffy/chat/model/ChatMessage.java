package com.meloffy.chat.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.meloffy.chat.enums.MessageStatus;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatMessage {

    private String to;
    private String content;
    private String messageId;
    private String messageTime;
    private String from;
    private MessageStatus status;
    private long timestamp;
    private String clientTempId;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getClientTempId() {
        return clientTempId;
    }

    public void setClientTempId(String clientTempId) {
        this.clientTempId = clientTempId;
    }
}