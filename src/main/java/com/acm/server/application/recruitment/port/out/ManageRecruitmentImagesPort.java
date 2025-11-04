package com.acm.server.application.recruitment.port.out;

import com.acm.server.domain.RecruitmentImage;

import java.util.List;
import java.util.Optional;

public interface ManageRecruitmentImagesPort {
    Optional<RecruitmentImage> findByRecruitmentIdAndUrl(Long recruitmentId, String imageUrl);

    RecruitmentImage save(RecruitmentImage image);

    void deleteByRecruitmentIdAndUrl(Long recruitmentId, String imageUrl);

    List<RecruitmentImage> findAllByRecruitmentId(Long recruitmentId);

    void deleteAllByRecruitmentId(Long recruitmentId);
}
