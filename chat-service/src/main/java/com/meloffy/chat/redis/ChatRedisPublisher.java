package com.meloffy.chat.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class ChatRedisPublisher {

    private final StringRedisTemplate stringRedisTemplate;

    public ChatRedisPublisher(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void publishToUser(String userId, String messageJson) {
        String channel = "chat:user:" + userId;
        stringRedisTemplate.convertAndSend(channel, messageJson);
    }
}
