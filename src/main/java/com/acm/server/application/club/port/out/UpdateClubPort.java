package com.acm.server.application.club.port.out;

import com.acm.server.application.club.dto.UpdateClubReq;
import com.acm.server.domain.Club;

public interface UpdateClubPort {
    Club updateClubById(Long clubId, UpdateClubReq req);
}
