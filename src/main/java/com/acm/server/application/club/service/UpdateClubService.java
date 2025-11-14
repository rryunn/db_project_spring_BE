package com.acm.server.application.club.service;

import com.acm.server.application.club.dto.UpdateClubReq;
import com.acm.server.application.club.port.in.UpdateClubUseCase;
import com.acm.server.application.club.port.out.ClubRedisPort;
import com.acm.server.application.club.port.out.UpdateClubPort;
import com.acm.server.domain.Club;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateClubService implements UpdateClubUseCase {

    private final UpdateClubPort updateClubPort;
    private final ClubRedisPort clubRedisPort;

    @Transactional
    @Override
    public Club updateClub(Long clubId, UpdateClubReq req) {
        Club club = updateClubPort.updateClubById(clubId, req);
        evictCaches(club);
        return club;
    }

    private void evictCaches(Club club) {
        switch (club.getClubType()) {
            case "중앙동아리" -> clubRedisPort.evictCentralClub(club.getId());
            case "소학회" -> clubRedisPort.evictAcademicClub(club.getId());
            default -> { /* 혹시 또 다른 타입 생길 때 대비 */ }
        }
    }
}
