package com.meloffy.snippix.auth_service.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RefreshTokenRedisService {

    private static final String PREFIX = "refresh_token:";

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenRedisService.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void save(String keycloakUserId, String refreshToken, Duration ttl) {
        try {
            redisTemplate.opsForValue()
                    .set(PREFIX + refreshToken, keycloakUserId, ttl);

            log.info(
                    "[REFRESH-TOKEN][SAVE] refreshToken={} userId={} ttl={}s",
                    mask(refreshToken),
                    keycloakUserId,
                    ttl.getSeconds()
            );
        } catch (Exception ex) {
            log.error(
                    "[REFRESH-TOKEN][SAVE-FAILED] refreshToken={}",
                    mask(refreshToken),
                    ex
            );
            throw ex;
        }
    }

    public String get(String refreshToken) {
        try {
            Object val =
                    redisTemplate.opsForValue().get(PREFIX + refreshToken);

            if (val == null) {
                log.warn(
                        "[REFRESH-TOKEN][MISS] refreshToken={}",
                        mask(refreshToken)
                );
                return null;
            }

            log.info(
                    "[REFRESH-TOKEN][HIT] refreshToken={}",
                    mask(refreshToken)
            );
            return val.toString();

        } catch (Exception ex) {
            log.error(
                    "[REFRESH-TOKEN][GET-FAILED] userId={}",
                    mask(refreshToken),
                    ex
            );
            throw ex;
        }
    }

    public void delete(String refreshToken) {
        try {
            Boolean deleted =
                    redisTemplate.delete(PREFIX + refreshToken);

            if (Boolean.TRUE.equals(deleted)) {
                log.info(
                        "[REFRESH-TOKEN][DELETE] refreshToken={}",
                        mask(refreshToken)
                );
            } else {
                log.warn(
                        "[REFRESH-TOKEN][DELETE-MISS] refreshToken={}",
                        mask(refreshToken)
                );
            }
        } catch (Exception ex) {
            log.error(
                    "[REFRESH-TOKEN][DELETE-FAILED] refreshToken={}",
                    mask(refreshToken),
                    ex
            );
            throw ex;
        }
    }

    public void rotate(
            String oldRefreshToken,
            String newRefreshToken,
            String userId,
            Duration ttl
    ) {
        try {
            // Delete old refresh token
            redisTemplate.delete(PREFIX + oldRefreshToken);

            // Save new refresh token
            redisTemplate.opsForValue()
                    .set(PREFIX + newRefreshToken, userId, ttl);

            log.info(
                    "[REFRESH-TOKEN][ROTATE] userId={} ttl={}s",
                    userId,
                    ttl.getSeconds()
            );

        } catch (Exception ex) {
            log.error(
                    "[REFRESH-TOKEN][ROTATE-FAILED] userId={}",
                    userId,
                    ex
            );
            throw ex;
        }
    }

    private String mask(String token) {
        return token == null ? "null"
                : token.substring(0, 6) + "...";
    }
}

