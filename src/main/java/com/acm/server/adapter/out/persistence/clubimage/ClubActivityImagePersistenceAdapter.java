package com.acm.server.adapter.out.persistence.clubimage;

import com.acm.server.adapter.out.entity.ClubActivityImageEntity;
import com.acm.server.adapter.out.entity.ClubEntity;
import com.acm.server.adapter.out.persistence.club.JpaClubRepository;
import com.acm.server.adapter.out.persistence.clubimage.JpaClubActivityImageJpaRepository;
import com.acm.server.application.clubimage.port.out.LoadClubActivityImagesPort;
import com.acm.server.application.clubimage.port.out.ManageClubActivityImagesPort;
import com.acm.server.domain.ClubActivityImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ClubActivityImagePersistenceAdapter implements LoadClubActivityImagesPort, ManageClubActivityImagesPort  {

    private final JpaClubActivityImageJpaRepository jpaClubActivityImageJpaRepository;
    private final JpaClubRepository jpaClubRepository; // FK 참조용 (getReferenceById)

    @Override
    public List<ClubActivityImage> loadByClubId(Long clubId) {
        return jpaClubActivityImageJpaRepository.findAllByClub_Id(clubId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<ClubActivityImage> findAllByClubIdOrderByIdAsc(Long clubId) {
        // PK 오름차순으로 조회
        return jpaClubActivityImageJpaRepository.findAllByClub_Id(clubId)
                .stream()
                .sorted(java.util.Comparator.comparing(ClubActivityImageEntity::getId))
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<ClubActivityImage> findByClubIdAndUrl(Long clubId, String imageUrl) {
        return jpaClubActivityImageJpaRepository.findAllByClub_Id(clubId).stream()
                .filter(e -> e.getImageUrl().equals(imageUrl))
                .findFirst()
                .map(this::toDomain);
    }

    @Override
    public ClubActivityImage save(ClubActivityImage image) {
        ClubEntity clubRef = jpaClubRepository.getReferenceById(image.getClubId());
        ClubActivityImageEntity entity = ClubActivityImageEntity.builder()
                .id(image.getId())
                .club(clubRef)
                .imageUrl(image.getImageUrl())
                .build();

        ClubActivityImageEntity saved = jpaClubActivityImageJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void deleteByClubIdAndUrl(Long clubId, String imageUrl) {
        var all = jpaClubActivityImageJpaRepository.findAllByClub_Id(clubId);
        all.stream()
                .filter(e -> e.getImageUrl().equals(imageUrl))
                .findFirst()
                .ifPresent(jpaClubActivityImageJpaRepository::delete);
    }

    @Override
    public List<ClubActivityImage> findAllByClubId(Long clubId) {
        return jpaClubActivityImageJpaRepository.findAllByClub_Id(clubId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public void deleteAllByClubId(Long clubId) {
        var all = jpaClubActivityImageJpaRepository.findAllByClub_Id(clubId);
        jpaClubActivityImageJpaRepository.deleteAll(all);
    }

    private ClubActivityImage toDomain(ClubActivityImageEntity e) {
        return ClubActivityImage.builder()
                .id(e.getId())
                .clubId(e.getClub().getId())
                .imageUrl(e.getImageUrl())
                .build();
    }
}