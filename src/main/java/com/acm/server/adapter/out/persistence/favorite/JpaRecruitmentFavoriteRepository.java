package com.acm.server.adapter.out.persistence.favorite;


import com.acm.server.adapter.out.entity.RecruitmentFavoriteEntity;
import com.acm.server.adapter.out.entity.RecruitmentFavoriteId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaRecruitmentFavoriteRepository extends JpaRepository<RecruitmentFavoriteEntity, RecruitmentFavoriteId> {
    List<RecruitmentFavoriteEntity> findAllByUser_Id(Long userId);
}
