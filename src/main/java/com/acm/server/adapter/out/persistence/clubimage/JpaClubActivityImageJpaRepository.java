// com/acm/server/adapter/out/persistence/clubimage/JpaClubActivityImageJpaRepository.java
package com.acm.server.adapter.out.persistence.clubimage;

import com.acm.server.adapter.out.entity.ClubActivityImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaClubActivityImageJpaRepository extends JpaRepository<ClubActivityImageEntity, Long> {

    /** club_id 기준으로 전체 조회 */
    List<ClubActivityImageEntity> findAllByClub_Id(Long clubId);

    /** club_id + image_url 기준으로 단일 이미지 조회 */
    Optional<ClubActivityImageEntity> findByClub_IdAndImageUrl(Long clubId, String imageUrl);

    /** club_id + image_url 기준으로 단일 이미지 삭제 */
    long deleteByClub_IdAndImageUrl(Long clubId, String imageUrl);

    /** club_id 기준으로 모든 이미지 삭제 */
    void deleteAllByClub_Id(Long clubId);
}
