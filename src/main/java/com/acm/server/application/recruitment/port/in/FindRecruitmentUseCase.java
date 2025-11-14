package com.acm.server.application.recruitment.port.in;

import com.acm.server.adapter.out.entity.RecruitmentImage;
import com.acm.server.domain.Recruitment;

import java.util.List;
import java.util.Optional;

public interface FindRecruitmentUseCase {

    List<Recruitment> findAllRecruitment();
    List<Recruitment> findRecruitmentsByClubId(Long clubId);
    Optional<Recruitment> findRecruitmentById(Long recruitmentId);
    Optional<Recruitment> findRecruitmentByIdWithView(Long recruitmentId, Long userId);
    void deleteRecruitmentById(Long recruitmentId);
    List<Recruitment> getMainRecruitment();
    List<String> getRecruitmentImageUrls(Long id);
}
