package com.acm.server.adapter.out.persistence.clubimage;

import com.acm.server.adapter.out.entity.ClubActivityImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaClubActivityImageJpaRepository extends JpaRepository<ClubActivityImageEntity, Long> {
    List<ClubActivityImageEntity> findAllByClub_Id(Long clubId); // club 필드의 id로 조회
}