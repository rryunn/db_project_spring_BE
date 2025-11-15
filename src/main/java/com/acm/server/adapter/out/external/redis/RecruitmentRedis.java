package com.acm.server.adapter.out.external.redis;

import com.acm.server.application.recruitment.port.out.RecruitmentRedisPort;
import com.acm.server.domain.Recruitment;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RecruitmentRedis implements RecruitmentRedisPort {

    private static final String ALL_KEY = "recruitment:list:all";
    private static final String MAIN_KEY = "recruitment:list:main";
    private static final Duration TTL = Duration.ofMinutes(5); // 리스트 캐시 TTL

    // ===== 상세 캐시 관련 상수 =====
    private static final String DETAIL_KEY_PREFIX = "recruitment:detail:";
    private static final String DETAIL_LRU_ZSET_KEY = "recruitment:detail:lru";
    private static final int DETAIL_CACHE_MAX_SIZE = 100;

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    // ================= 리스트 캐시 =================

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

    // ================= 상세 캐시 (100개 + LRU) =================

    @Override
    public Optional<Recruitment> getRecruitment(Long recruitmentId) {
        String key = DETAIL_KEY_PREFIX + recruitmentId;
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) {
            return Optional.empty();
        }

        try {
            Recruitment r = objectMapper.readValue(json, Recruitment.class);

            // LRU 갱신: 마지막 접근 시간 업데이트
            touchDetailLru(recruitmentId);

            // 사이즈 제한 적용
            enforceDetailCacheLimit();

            return Optional.of(r);
        } catch (Exception e) {
            // 파싱 실패 시 키 삭제 + miss 처리
            redisTemplate.delete(key);
            removeFromDetailLru(recruitmentId);
            return Optional.empty();
        }
    }

    @Override
    public void setRecruitment(Long recruitmentId, Recruitment recruitment) {
        if (recruitment == null) return;
        String key = DETAIL_KEY_PREFIX + recruitmentId;
        try {
            String json = objectMapper.writeValueAsString(recruitment);
            redisTemplate.opsForValue().set(key, json);
            // LRU 갱신
            touchDetailLru(recruitmentId);
            // 사이즈 제한 적용
            enforceDetailCacheLimit();
        } catch (Exception e) {
            // 실패 시 그냥 무시
        }
    }

    @Override
    public void evictRecruitment(Long recruitmentId) {
        String key = DETAIL_KEY_PREFIX + recruitmentId;
        redisTemplate.delete(key);
        removeFromDetailLru(recruitmentId);
    }

    // ================= 내부 공통 메서드 (리스트) =================

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
            // 실패 시 무시
        }
    }

    // ================= LRU 유틸 (상세 캐시 전용) =================

    /**
     * 상세 캐시에 대해 LRU 정보 갱신
     * - get 또는 set 시마다 호출
     */
    private void touchDetailLru(Long recruitmentId) {
        String member = recruitmentId.toString();
        double score = (double) System.currentTimeMillis();
        redisTemplate.opsForZSet().add(DETAIL_LRU_ZSET_KEY, member, score);
    }

    /**
     * LRU에서 특정 id 제거 (명시적 evict 시 사용)
     */
    private void removeFromDetailLru(Long recruitmentId) {
        String member = recruitmentId.toString();
        redisTemplate.opsForZSet().remove(DETAIL_LRU_ZSET_KEY, member);
    }

    /**
     * 상세 캐시가 100개를 넘으면
     * - ZSET에서 가장 오래된 것들부터 꺼내서
     * - 실제 detail 키와 함께 삭제
     */
    private void enforceDetailCacheLimit() {
        Long size = redisTemplate.opsForZSet().zCard(DETAIL_LRU_ZSET_KEY);
        if (size == null || size <= DETAIL_CACHE_MAX_SIZE) return;

        long over = size - DETAIL_CACHE_MAX_SIZE;
        // score 낮은 것(가장 오래된 것)부터 over개
        Set<String> evictIds = redisTemplate.opsForZSet()
                .range(DETAIL_LRU_ZSET_KEY, 0, over - 1);
        if (evictIds == null || evictIds.isEmpty()) return;

        for (String idStr : evictIds) {
            String detailKey = DETAIL_KEY_PREFIX + idStr;
            redisTemplate.delete(detailKey);
        }
        // ZSET에서도 제거
        redisTemplate.opsForZSet().remove(DETAIL_LRU_ZSET_KEY, evictIds.toArray());
    }
}
