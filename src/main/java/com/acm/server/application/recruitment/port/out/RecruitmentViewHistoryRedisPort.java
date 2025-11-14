package com.acm.server.application.recruitment.port.out;

public interface RecruitmentViewHistoryRedisPort {
    /**
     * (userId, recruitmentId) 조합으로
     * 24시간 안에 본 적이 없으면 기록하고 true 리턴,
     * 이미 본 적 있으면 false 리턴.
     */
    boolean markIfNotViewed(Long userId, Long recruitmentId);
}
