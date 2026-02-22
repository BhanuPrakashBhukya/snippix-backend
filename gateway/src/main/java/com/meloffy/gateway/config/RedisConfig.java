package com.meloffy.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        RedisStandaloneConfiguration config =
                new RedisStandaloneConfiguration("redis", 6379);
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public ReactiveStringRedisTemplate redisTemplate(ReactiveRedisConnectionFactory factory) {
        return new ReactiveStringRedisTemplate(factory);
    }

}
