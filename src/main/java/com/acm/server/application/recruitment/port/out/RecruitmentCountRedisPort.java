package com.acm.server.application.recruitment.port.out;

import java.util.Collection;
import java.util.Map;

public interface RecruitmentCountRedisPort {

    void putAllViewCounts(Map<Long, Long> viewCounts);

    void putAllSaveCounts(Map<Long, Long> saveCounts);

    // 나중에 상세조회, 찜 토글에서 사용
    void incrementView(Long recruitmentId, long delta);

    void incrementSave(Long recruitmentId, long delta);

    Map<Long, Long> getViewCounts(Collection<Long> recruitmentIds);

    Map<Long, Long> getSaveCounts(Collection<Long> recruitmentIds);
}
