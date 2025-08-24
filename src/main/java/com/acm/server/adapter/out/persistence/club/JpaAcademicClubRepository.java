package com.acm.server.adapter.out.persistence.club;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.acm.server.adapter.out.entity.AcademicClubEntity;

public interface JpaAcademicClubRepository extends JpaRepository<AcademicClubEntity, Long>{
    @Query("SELECT c, a " +
           "FROM AcademicClubEntity a " +
           "JOIN a.club c " +
           "WHERE c.id = :clubId")
    Optional<Object> findAcademicClubById(@Param("clubId") Long clubId);    
}