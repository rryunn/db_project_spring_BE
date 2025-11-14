package com.acm.server.adapter.out.external.redis;

import com.acm.server.application.recruitment.port.out.RecruitmentCountRedisPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RecruitmentCountRedis implements RecruitmentCountRedisPort {
    
    private static final String VIEW_COUNT_HASH_KEY = "recruitment:view_count";
    private static final String SAVE_COUNT_HASH_KEY = "recruitment:save_count";

    private final StringRedisTemplate redisTemplate;

    @Override
    public void putAllViewCounts(Map<Long, Long> viewCounts) {
        if (viewCounts == null || viewCounts.isEmpty()) return;

        Map<String, String> map = new HashMap<>(viewCounts.size());
        viewCounts.forEach((id, value) ->
                map.put(id.toString(), value.toString())
        );
        redisTemplate.opsForHash().putAll(VIEW_COUNT_HASH_KEY, map);
    }

    @Override
    public void putAllSaveCounts(Map<Long, Long> saveCounts) {
        if (saveCounts == null || saveCounts.isEmpty()) return;

        Map<String, String> map = new HashMap<>(saveCounts.size());
        saveCounts.forEach((id, value) ->
                map.put(id.toString(), value.toString())
        );
        redisTemplate.opsForHash().putAll(SAVE_COUNT_HASH_KEY, map);
    }

    // 나중에 상세조회, 찜/찜취소에서 쓸 수 있는 increment도 미리 만들어두면 좋음
    @Override
    public void incrementView(Long recruitmentId, long delta) {
        redisTemplate.opsForHash().increment(
                VIEW_COUNT_HASH_KEY,
                recruitmentId.toString(),
                delta
        );
    }

    @Override
    public void incrementSave(Long recruitmentId, long delta) {
        redisTemplate.opsForHash().increment(
                SAVE_COUNT_HASH_KEY,
                recruitmentId.toString(),
                delta
        );
    }

    @Override
    public Map<Long, Long> getViewCounts(Collection<Long> recruitmentIds) {
        return getCounts(VIEW_COUNT_HASH_KEY, recruitmentIds);
    }

    @Override
    public Map<Long, Long> getSaveCounts(Collection<Long> recruitmentIds) {
        return getCounts(SAVE_COUNT_HASH_KEY, recruitmentIds);
    }

    private Map<Long, Long> getCounts(String hashKey, Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyMap();
        }

        // HMGET recruitment:view_count id1 id2 ...
        var stringIds = ids.stream()
                .map(String::valueOf)
                .toList();

        List<Object> rawValues = redisTemplate.opsForHash()
                .multiGet(hashKey, new ArrayList<>(stringIds));

        Map<Long, Long> result = new HashMap<>();
        int i = 0;
        for (Long id : ids) {
            Object v = rawValues.get(i++);
            if (v == null) continue;
            try {
                result.put(id, Long.valueOf(v.toString()));
            } catch (NumberFormatException ignored) {
            }
        }
        return result;
    }
}
