package com.meloffy.chat.config;

import com.meloffy.chat.redis.ChatRedisSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisSubscriberConfig {

    @Bean
    public RedisMessageListenerContainer redisContainer(
            RedisConnectionFactory factory,
            ChatRedisSubscriber subscriber) {

        RedisMessageListenerContainer container =
                new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);

        // pattern subscription
        container.addMessageListener(
                subscriber,
                new PatternTopic("chat:user:*")
        );

        return container;
    }

}
