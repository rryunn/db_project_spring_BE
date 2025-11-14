package com.acm.server.adapter.out.external.redis;

import org.springframework.data.redis.core.StringRedisTemplate;

import com.acm.server.application.recruitment.port.out.RecruitmentRedisPort;
import com.acm.server.domain.Recruitment;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RecruitmentRedis implements RecruitmentRedisPort{
    private static final String ALL_KEY = "recruitment:list:all";
    private static final String MAIN_KEY = "recruitment:list:main";
    private static final Duration TTL = Duration.ofMinutes(5); // 예시: 5분 캐시

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<List<Recruitment>> getAllRecruitments() {
        return getList(ALL_KEY);
    }

    @Override
    public void setAllRecruitments(List<Recruitment> recruitments) {
        setList(ALL_KEY, recruitments);
    }

    @Override
    public Optional<List<Recruitment>> getMainRecruitments() {
        return getList(MAIN_KEY);
    }

    @Override
    public void setMainRecruitments(List<Recruitment> recruitments) {
        setList(MAIN_KEY, recruitments);
    }

    @Override
    public void evictAll() {
        redisTemplate.delete(ALL_KEY);
    }

    @Override
    public void evictMain() {
        redisTemplate.delete(MAIN_KEY);
    }

    // ===== 내부 공통 메서드 =====

    private Optional<List<Recruitment>> getList(String key) {
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) return Optional.empty();

        try {
            List<Recruitment> list = objectMapper.readValue(
                    json,
                    new TypeReference<List<Recruitment>>() {}
            );
            return Optional.of(list);
        } catch (Exception e) {
            // 파싱 실패하면 캐시 날리고 miss 처리
            redisTemplate.delete(key);
            return Optional.empty();
        }
    }

    private void setList(String key, List<Recruitment> recruitments) {
        if (recruitments == null) return;
        try {
            String json = objectMapper.writeValueAsString(recruitments);
            redisTemplate.opsForValue().set(key, json, TTL);
        } catch (Exception e) {
            // 실패 시 그냥 로그만 찍고 무시해도 됨
        }
    }
}
