package com.acm.server.adapter.out.persistence.club;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.acm.server.adapter.out.entity.CentralClubEntity;

public interface JpaCentralClubRepository extends JpaRepository<CentralClubEntity, Long>{
    @Query("SELECT c, d " +
           "FROM CentralClubEntity d " +
           "JOIN d.club c " +
           "WHERE c.id = :clubId")
    Optional<Object> findCentralClubById(@Param("clubId") Long clubId);
}
