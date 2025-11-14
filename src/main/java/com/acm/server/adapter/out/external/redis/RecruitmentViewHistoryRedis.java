package com.acm.server.adapter.out.external.redis;

import com.acm.server.application.recruitment.port.out.RecruitmentViewHistoryRedisPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RecruitmentViewHistoryRedis implements RecruitmentViewHistoryRedisPort {
    private final StringRedisTemplate redisTemplate;

    private static final String KEY_PATTERN = "recruit:view:user:%d:%d";
    private static final Duration TTL = Duration.ofHours(24);

    @Override
    public boolean markIfNotViewed(Long userId, Long recruitmentId) {
        String key = KEY_PATTERN.formatted(recruitmentId, userId);

        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, "1", TTL);

        // setIfAbsent == true  → 처음 보는 것 → 조회수 +1 해야 함
        // setIfAbsent == false → 이미 본 적 있음 → 조회수 증가 X
        return Boolean.TRUE.equals(success);
    }
}
