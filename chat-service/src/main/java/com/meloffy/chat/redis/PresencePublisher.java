package com.meloffy.chat.redis;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class PresencePublisher {

    private final StringRedisTemplate redis;

    public PresencePublisher(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public void publish(String userId, String status) {
        ObjectNode msg = JsonNodeFactory.instance.objectNode();
        msg.put("type", "PRESENCE");
        msg.put("userId", userId);
        msg.put("status", status);

        redis.convertAndSend("presence:user", msg.toString());
    }
}
