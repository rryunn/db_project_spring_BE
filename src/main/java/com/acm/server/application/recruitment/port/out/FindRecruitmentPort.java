package com.acm.server.application.recruitment.port.out;
import com.acm.server.application.recruitment.dto.RecruitmentCounterProjection;
import com.acm.server.domain.Recruitment;

import java.util.List;
import java.util.Optional;

public interface FindRecruitmentPort {
    List<Recruitment> findAllRecruitment();
    Optional<Recruitment> findRecruitmentById(Long recruitmentId);
    void deleteRecruitmentById(Long recruitmentId);
    List<Recruitment> getMainRecruitment();
    List<String> getRecruitmentImageUrls(Long id);
    List<Recruitment> findRecruitmentsByClubId(Long clubId);
    List<Recruitment> findAllRecruitmentForRedis();

    //캐싱용도
    List<RecruitmentCounterProjection> findAllForCounterInit();
}
