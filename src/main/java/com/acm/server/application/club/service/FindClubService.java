package com.acm.server.application.club.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.acm.server.application.club.port.in.FindClubUseCase;
import com.acm.server.application.club.port.out.FindClubPort;
import com.acm.server.domain.AcademicClub;
import com.acm.server.domain.CentralClub;
import com.acm.server.domain.Club;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FindClubService implements FindClubUseCase{

    private final FindClubPort findClubPort;

    @Override
    public List<Club> findAllClub() {
        return findClubPort.findAllClub();
    }

    @Override
    public CentralClub findCentralClub(Long clubId) {
        return findClubPort.findCentralClub(clubId);
    }

    @Override
    public AcademicClub findAcademicClub(Long clubId) {
        return findClubPort.findAcademicClub(clubId);
    }

    @Override
    public List<Club> findFilterClub(String type, String category,Boolean isRecruiting, String department){
        
        // 중앙동아리일 경우 department 무시
        if ("CENTRAL".equalsIgnoreCase(type)) {
            department = null;
        }
        return findClubPort.findFilterClub(type, category, isRecruiting, department);
    }
}
