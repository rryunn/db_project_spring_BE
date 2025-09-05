package com.acm.server.application.clubimage.service;

import com.acm.server.application.club.port.out.FindClubPort;
import com.acm.server.application.clubimage.port.in.FindClubActivityImagesUseCase;
import com.acm.server.application.clubimage.port.out.LoadClubActivityImagesPort;
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

    @Override
    public List<ClubActivityImage> findByClubId(Long clubId) {
        findClubPort.findClub(clubId)
            .orElseThrow(() -> new ResourceNotFoundException("Central club not found: " + clubId));
        return loadPort.loadByClubId(clubId); // 클럽 존재 검증은 필요시 추가
    }
}
