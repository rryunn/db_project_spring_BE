package com.acm.server.adapter.out.external.redis;

import com.acm.server.application.clubimage.port.out.ClubActivityImageRedisPort;
import com.acm.server.domain.ClubActivityImage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ClubActivityImageRedis implements ClubActivityImageRedisPort {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String KEY_PREFIX = "club:activity:"; // club:activity:{clubId}
    private static final Duration TTL = Duration.ofHours(6);   // 필요하면 변경

    private String key(Long clubId) {
        return KEY_PREFIX + clubId;
    }

    @Override
    public Optional<List<ClubActivityImage>> getImages(Long clubId) {
        String redisKey = key(clubId);
        String json = redisTemplate.opsForValue().get(redisKey);

        if (json == null || json.isBlank()) {
            return Optional.empty();
        }

        try {
            List<ClubActivityImage> list = objectMapper.readValue(
                    json,
                    new TypeReference<List<ClubActivityImage>>() {}
            );

            if (list == null) {
                return Optional.of(Collections.emptyList());
            }

            return Optional.of(list);

        } catch (Exception e) {
            // 역직렬화 실패 시 캐시 제거
            redisTemplate.delete(redisKey);
            return Optional.empty();
        }
    }

    @Override
    public void saveImages(Long clubId, List<ClubActivityImage> images) {
        String redisKey = key(clubId);

        try {
            String json = objectMapper.writeValueAsString(images);
            redisTemplate.opsForValue().set(redisKey, json, TTL);

        } catch (Exception e) {
            // 캐시 저장 실패는 치명적이지 않음. 무시(로그 추천)
        }
    }

    @Override
    public void evictImages(Long clubId) {
        redisTemplate.delete(key(clubId));
    }
}
