package com.meloffy.gateway.utils;

import com.meloffy.gateway.config.RateLimitProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> script;
    private final RateLimitProperties properties;

    public RateLimitFilter(
            @Qualifier("reactiveStringRedisTemplate")
            ReactiveStringRedisTemplate redisTemplate,
            RateLimitProperties properties
    ) {
        this.redisTemplate = redisTemplate;
        this.properties = properties;
        this.script = new DefaultRedisScript<>();
        this.script.setLocation(new ClassPathResource("ratelimit.lua"));
        this.script.setResultType(Long.class);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String rawPath = exchange.getRequest().getPath().value();
        String path = normalizePath(rawPath);
        String key = buildKey(exchange, path);
        long now = System.currentTimeMillis();

        RateLimitProperties.Rule rule = resolveRule(path);

        List<Object> args = List.of(
                String.valueOf(rule.getMaxTokens()),
                String.valueOf(rule.getRefillRate()),
                String.valueOf(now),
                String.valueOf(rule.getTtlSeconds())
        );

        return redisTemplate.execute(
                        script,
                        List.of(key),
                        args
                )
                .next()
                .onErrorReturn(1L)      // Redis error → allow
                .defaultIfEmpty(1L)    // Redis empty → allow
                .flatMap(allowed -> {
                    if (allowed == 1L) {
                        return chain.filter(exchange);
                    }
                    exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                    return exchange.getResponse().setComplete();
                });
    }

    private RateLimitProperties.Rule resolveRule(String path) {
        return properties.getRules().entrySet().stream()
                .filter(entry -> path.startsWith(entry.getKey()))
                .map(entry -> entry.getValue())
                .findFirst()
                .orElse(defaultRule());
    }

    private RateLimitProperties.Rule defaultRule() {
        RateLimitProperties.Rule rule = new RateLimitProperties.Rule();
        rule.setMaxTokens(50);
        rule.setRefillRate(1);
        rule.setTtlSeconds(60);
        return rule;
    }

    private String normalizePath(String path) {
        if (path.startsWith("/api/users")) {
            return "/api/users";
        }
        if (path.startsWith("/api/chat")) {
            return "/api/chat";
        }
        return path;
    }

    private String buildKey(ServerWebExchange exchange, String path) {

        String userId = exchange.getRequest()
                .getHeaders()
                .getFirst("X-User-Id");

        if (userId != null) {
            return "rl:user:" + userId + ":" + path;
        }

        String ip = exchange.getRequest()
                .getRemoteAddress()
                .getAddress()
                .getHostAddress();

        return "rl:ip:" + ip + ":" + path;
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
