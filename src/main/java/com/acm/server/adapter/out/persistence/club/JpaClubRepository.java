package com.acm.server.adapter.out.persistence.club;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.acm.server.adapter.out.entity.ClubEntity;

public interface JpaClubRepository extends JpaRepository<ClubEntity, Long> {
    List<ClubEntity> findAll();
}
