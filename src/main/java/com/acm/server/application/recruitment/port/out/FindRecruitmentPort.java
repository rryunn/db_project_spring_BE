package com.acm.server.application.recruitment.port.out;

import com.acm.server.adapter.out.entity.RecruitmentImage;
import com.acm.server.domain.Recruitment;

import java.util.List;
import java.util.Optional;

public interface FindRecruitmentPort {
    List<Recruitment> findAllRecruitment();
    Optional<Recruitment> findRecruitmentByClubId(Long clubId);
    void deleteRecruitmentById(Long clubId);
    List<Recruitment> getMainRecruitment();
    List<String> getRecruitmentImageUrls(Long id);
}
