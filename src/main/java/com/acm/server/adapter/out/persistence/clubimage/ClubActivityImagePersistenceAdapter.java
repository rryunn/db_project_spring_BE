package com.acm.server.adapter.out.persistence.clubimage;

import com.acm.server.adapter.out.entity.ClubActivityImageEntity;
import com.acm.server.adapter.out.persistence.clubimage.JpaClubActivityImageJpaRepository;
import com.acm.server.application.clubimage.port.out.LoadClubActivityImagesPort;
import com.acm.server.domain.ClubActivityImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ClubActivityImagePersistenceAdapter implements LoadClubActivityImagesPort {

    private final JpaClubActivityImageJpaRepository repo;

    @Override
    public List<ClubActivityImage> loadByClubId(Long clubId) {
        return repo.findAllByClub_Id(clubId).stream()
                .map(this::toDomain)
                .toList();
    }

    private ClubActivityImage toDomain(ClubActivityImageEntity e) {
        return ClubActivityImage.builder()
                .id(e.getId())
                .clubId(e.getClub().getId())
                .imageUrl(e.getImageUrl())
                .build();
    }
}