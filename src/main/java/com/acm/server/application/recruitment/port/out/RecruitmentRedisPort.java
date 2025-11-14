package com.acm.server.application.recruitment.port.out;

import java.util.List;
import java.util.Optional;

import com.acm.server.domain.Recruitment;

public interface RecruitmentRedisPort {
    // 전체 리스트 캐시
    Optional<List<Recruitment>> getAllRecruitments();
    void setAllRecruitments(List<Recruitment> recruitments);

    // 모집중(메인) 리스트 캐시
    Optional<List<Recruitment>> getMainRecruitments();
    void setMainRecruitments(List<Recruitment> recruitments);

    // 필요하면 무효화용 메서드도 추가 가능
    void evictAll();
    void evictMain();
}
