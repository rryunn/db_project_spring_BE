package com.acm.server.application.recruitment.port.out;

import java.util.List;
import java.util.Optional;

public interface RecruitmentImageRedisPort {

    /**
     * 모집공고별 이미지 URL 리스트 조회 (캐시)
     */
    Optional<List<String>> getImageUrls(Long recruitmentId);

    /**
     * 모집공고별 이미지 URL 리스트 저장 (캐시)
     */
    void setImageUrls(Long recruitmentId, List<String> urls);

    /**
     * 해당 모집공고 이미지 리스트 캐시 무효화
     */
    void evictImageUrls(Long recruitmentId);
}
