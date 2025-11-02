package com.acm.server.application.club.service;

import com.acm.server.application.club.dto.UpdateClubReq;
import com.acm.server.application.club.port.in.UpdateClubUseCase;
import com.acm.server.application.club.port.out.UpdateClubPort;
import com.acm.server.domain.Club;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateClubService implements UpdateClubUseCase {

    private final UpdateClubPort updateClubPort;

    @Transactional
    @Override
    public Club updateClub(Long clubId, UpdateClubReq req) {
        return updateClubPort.updateClubById(clubId, req);
    }
}
