package com.acm.server.adapter.out.external.redis;

import com.acm.server.application.recruitment.port.out.RecruitmentImageRedisPort;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecruitmentImageRedis implements RecruitmentImageRedisPort {

    private static final String KEY_PREFIX = "recruitment:image:list:";
    private static final Duration TTL = Duration.ofMinutes(5);  // 예시: 5분 캐시

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<List<String>> getImageUrls(Long recruitmentId) {
        String key = KEY_PREFIX + recruitmentId;
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) return Optional.empty();

        try {
            List<String> list = objectMapper.readValue(
                    json,
                    new TypeReference<List<String>>() {}
            );
            return Optional.of(list);
        } catch (Exception e) {
            // 파싱 실패 시 캐시 제거 + miss 처리
            redisTemplate.delete(key);
            return Optional.empty();
        }
    }

    @Override
    public void setImageUrls(Long recruitmentId, List<String> urls) {
        String key = KEY_PREFIX + recruitmentId;

        if (urls == null) {
            redisTemplate.delete(key);
            return;
        }

        try {
            String json = objectMapper.writeValueAsString(urls);
            redisTemplate.opsForValue().set(key, json, TTL);
        } catch (Exception e) {
            // 실패 시 그냥 무시 (캐시는 best-effort)
        }
    }

    @Override
    public void evictImageUrls(Long recruitmentId) {
        String key = KEY_PREFIX + recruitmentId;
        redisTemplate.delete(key);
    }
}
