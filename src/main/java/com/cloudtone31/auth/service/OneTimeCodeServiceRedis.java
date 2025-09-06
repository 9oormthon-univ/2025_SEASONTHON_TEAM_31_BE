package com.cloudtone31.auth.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OneTimeCodeServiceRedis implements OneTimeCodeService {

    private final StringRedisTemplate redisTemplate;

    private static final String PREFIX = "otc:"; // one-time-code prefix

    /**
     * 원타임 코드를 발급하고 Redis에 userId와 TTL을 저장합니다.
     *
     * @param userId 사용자 식별자
     * @param ttl    만료 시간 (예: Duration.ofMinutes(2))
     * @return 발급된 원타임 코드
     */
    @Override
    public String issue(String userId, Duration ttl) {
        // 랜덤 문자열 (UUID 기반)
        String code = UUID.randomUUID().toString().replace("-", "");
        String key = PREFIX + code;

        // Redis에 userId 저장 (TTL 적용)
        redisTemplate.opsForValue().set(key, userId, ttl);

        return code;
    }

    /**
     * 원타임 코드를 소비합니다. (1회성)
     * 유효하면 userId를 반환하고 Redis에서 삭제합니다.
     * 없거나 만료되었으면 null 반환.
     *
     * @param code 원타임 코드
     * @return userId or null
     */
    @Override
    public String consume(String code) {
        String key = PREFIX + code;

        String userId = redisTemplate.opsForValue().get(key);

        if (userId != null) {
            // 한 번 쓰면 바로 삭제
            redisTemplate.delete(key);
        }

        return userId;
    }
}
