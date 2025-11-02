package com.acm.server.adapter.out.persistence.club;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.acm.server.adapter.out.entity.ClubEntity;


public interface JpaClubRepository extends JpaRepository<ClubEntity, Long> {
    List<ClubEntity> findAll();
    @Query(value =
        "SELECT DISTINCT c.* " +
        "FROM clubs c " +
        "LEFT JOIN academic_club_details a ON c.club_id = a.club_id " +
        "LEFT JOIN departments d ON a.department_name = d.department_name " +
        "LEFT JOIN colleges co ON d.college_id = co.college_id " +
        "WHERE (:type IS NULL OR c.club_type = :type) " +
        "  AND (:category IS NULL OR c.club_category = :category) " +
        "  AND (:isRecruiting IS NULL OR c.is_recruiting = :isRecruiting) " +
        "  AND ( " +
        "        :department IS NULL " +
        "        OR (c.club_type = '소학회' AND ( " +
        "              a.department_name = :department " +
        "              OR a.department_name = ( " +
        "                   SELECT co2.college_name " +
        "                   FROM departments d2 " +
        "                   JOIN colleges co2 ON d2.college_id = co2.college_id " +
        "                   WHERE d2.department_name = :department " +
        "              ) " +
        "        )) " +
        "      )",
        nativeQuery = true)
    List<ClubEntity> findFilterClub(
        @Param("type") String type,
        @Param("category") String category,
        @Param("isRecruiting") Boolean isRecruiting,
        @Param("department") String department
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update ClubEntity c set c.logoUrl = :logoUrl, c.updatedAt = CURRENT_TIMESTAMP where c.id = :clubId")
    int updateLogoUrl(@Param("clubId") Long clubId, @Param("logoUrl") String logoUrl);
}
