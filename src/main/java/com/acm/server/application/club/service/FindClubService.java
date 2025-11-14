package com.acm.server.application.club.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.acm.server.application.club.port.in.FindClubUseCase;
import com.acm.server.application.club.port.out.ClubRedisPort;
import com.acm.server.application.club.port.out.FindClubPort;
import com.acm.server.common.exception.ResourceNotFoundException;
import com.acm.server.domain.AcademicClub;
import com.acm.server.domain.CentralClub;
import com.acm.server.domain.Club;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FindClubService implements FindClubUseCase {

    private final FindClubPort findClubPort;
    private final ClubRedisPort clubRedisPort;

    @Override
    public List<Club> findAllClub() {
        // 1. Redis 먼저
        return clubRedisPort.getAllClubs()
                .orElseGet(() -> {
                    List<Club> clubs = findClubPort.findAllClub();
                    clubRedisPort.saveAllClubs(clubs);
                    return clubs;
                });
    }

    @Override
    public CentralClub findCentralClub(Long clubId) {
        return clubRedisPort.getCentralClub(clubId)
                .orElseGet(() -> {
                    CentralClub club = findClubPort.findCentralClub(clubId)
                            .orElseThrow(() -> new ResourceNotFoundException("Central club not found: " + clubId));
                    clubRedisPort.saveCentralClub(club);
                    return club;
                });
    }

    @Override
    public AcademicClub findAcademicClub(Long clubId) {
        return clubRedisPort.getAcademicClub(clubId)
                .orElseGet(() -> {
                    AcademicClub club = findClubPort.findAcademicClub(clubId)
                            .orElseThrow(() -> new ResourceNotFoundException("Academic club not found: " + clubId));
                    clubRedisPort.saveAcademicClub(club);
                    return club;
                });
    }

    @Override
    public List<Club> findFilterClub(String type, String category, Boolean isRecruiting, String department) {
        if ("CENTRAL".equalsIgnoreCase(type)) {
            department = null;
        }
        // 필터는 조합이 너무 많으니 캐시 없이 DB 직행
        return findClubPort.findFilterClub(type, category, isRecruiting, department);
    }
}
