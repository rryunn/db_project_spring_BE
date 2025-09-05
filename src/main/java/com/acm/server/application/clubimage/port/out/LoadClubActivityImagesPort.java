package com.acm.server.application.clubimage.port.out;

import com.acm.server.domain.ClubActivityImage;
import java.util.List;

public interface LoadClubActivityImagesPort {
    List<ClubActivityImage> loadByClubId(Long clubId);
}
