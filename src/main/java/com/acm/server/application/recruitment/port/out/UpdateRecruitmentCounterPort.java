package com.acm.server.application.recruitment.port.out;

import java.util.Map;

public interface UpdateRecruitmentCounterPort {

    /**
     * recruitmentId -> viewCount / saveCount 매핑을 받아서
     * DB의 카운터 값을 갱신한다.
     */
    void updateCounters(Map<Long, Long> viewCounts, Map<Long, Long> saveCounts);
}
