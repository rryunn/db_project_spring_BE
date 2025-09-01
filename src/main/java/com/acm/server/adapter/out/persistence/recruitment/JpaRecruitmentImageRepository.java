package com.acm.server.adapter.out.persistence.recruitment;

import com.acm.server.adapter.out.entity.RecruitmentImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaRecruitmentImageRepository extends JpaRepository<RecruitmentImage, Long> {
    List<RecruitmentImage> findByRecruitment_Id(Long recruitmentId);
}
