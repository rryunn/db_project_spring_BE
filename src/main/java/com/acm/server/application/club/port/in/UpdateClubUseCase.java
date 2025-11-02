package com.acm.server.application.club.port.in;

import com.acm.server.application.club.dto.UpdateClubReq;

public interface UpdateClubUseCase {
    Object updateClub(Long clubId, UpdateClubReq req);
}
