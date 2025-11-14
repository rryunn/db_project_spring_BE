package com.acm.server.application.clubimage.service;

import com.acm.server.application.club.port.out.FindClubPort;
import com.acm.server.application.clubimage.port.in.FindClubActivityImagesUseCase;
import com.acm.server.application.clubimage.port.out.LoadClubActivityImagesPort;
import com.acm.server.application.clubimage.port.out.ClubActivityImageRedisPort;
import com.acm.server.common.exception.ResourceNotFoundException;
import com.acm.server.domain.ClubActivityImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindClubActivityImagesService implements FindClubActivityImagesUseCase {

    private final LoadClubActivityImagesPort loadPort;
    private final FindClubPort findClubPort;
    private final ClubActivityImageRedisPort redisPort;

    @Override
    public List<ClubActivityImage> findByClubId(Long clubId) {

        // 1) 클럽 존재 검증
        findClubPort.findClub(clubId)
            .orElseThrow(() -> new ResourceNotFoundException("Club not found: " + clubId));

        // 2) 캐시 조회
        var cached = redisPort.getImages(clubId);
        if (cached.isPresent()) return cached.get();

        // 3) 캐시 없으면 DB 조회
        List<ClubActivityImage> result = loadPort.loadByClubId(clubId);

        // 4) Redis 저장
        redisPort.saveImages(clubId, result);

        return result;
    }
}
