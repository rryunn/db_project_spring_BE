package com.acm.server.adapter.out.persistence.favorite;


import com.acm.server.adapter.out.entity.RecruitmentFavoriteEntity;
import com.acm.server.adapter.out.entity.RecruitmentFavoriteId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaRecruitmentFavoriteRepository extends JpaRepository<RecruitmentFavoriteEntity, RecruitmentFavoriteId> {
    List<RecruitmentFavoriteEntity> findAllByUser_Id(Long userId);
    
    boolean existsById(RecruitmentFavoriteId id);

    // 삭제된 row 수를 리턴해줌
    long deleteByUser_IdAndId_RecruitmentId(Long userId, Long recruitmentId);
    // ↑ 복합키 구조에 따라 이름은 맞춰줘야 함
}
