package com.acm.server.application.clubimage.port.out;

import java.util.List;
import java.util.Optional;

import com.acm.server.domain.ClubActivityImage;

public interface ClubActivityImageRedisPort {

    Optional<List<ClubActivityImage>> getImages(Long clubId);

    void saveImages(Long clubId, List<ClubActivityImage> images);

    void evictImages(Long clubId);
}
