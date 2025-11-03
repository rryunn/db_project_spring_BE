package com.acm.server.application.club.port.out;
import com.acm.server.domain.ClubActivityImage;

import java.util.List;
import java.util.Optional;

public interface ManageClubActivityImagesPort {

    List<ClubActivityImage> findAllByClubIdOrderByIdAsc(Long clubId);

    Optional<ClubActivityImage> findByClubIdAndUrl(Long clubId, String imageUrl);

    ClubActivityImage save(ClubActivityImage image);

    void deleteByClubIdAndUrl(Long clubId, String imageUrl);

    List<ClubActivityImage> findAllByClubId(Long clubId);

    void deleteAllByClubId(Long clubId);

}
