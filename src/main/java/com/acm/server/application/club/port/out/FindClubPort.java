package com.acm.server.application.club.port.out;

import java.util.List;

import com.acm.server.domain.Club;

public interface FindClubPort {
    public List<Club> findAllClub();
}
