package com.acm.server.application.clubimage.port.in;

import com.acm.server.domain.ClubActivityImage;
import java.util.List;

public interface FindClubActivityImagesUseCase {
    List<ClubActivityImage> findByClubId(Long clubId);
}
