package com.acm.server.adapter.out.persistence.recruitment;

import com.acm.server.adapter.out.entity.RecruitmentImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaRecruitmentImageRepository extends JpaRepository<RecruitmentImage, Long> {
    List<RecruitmentImage> findByRecruitment_Id(Long recruitmentId);
    Optional<RecruitmentImage> findTop1ByRecruitment_IdOrderByIdAsc(Long recruitmentId);
    // 1. (교체/삭제 시) 특정 URL로 엔티티 조회
    Optional<RecruitmentImage> findByRecruitment_IdAndImageUrl(Long recruitmentId, String imageUrl);

    // 2. (교체/삭제 시) 특정 URL로 엔티티 삭제
    void deleteByRecruitment_IdAndImageUrl(Long recruitmentId, String imageUrl);

    // 3. (전체 삭제 시) 모집공고 ID로 엔티티 일괄 삭제
    void deleteByRecruitment_Id(Long recruitmentId);
}
