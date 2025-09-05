package com.acm.server.application.club.port.out;

import java.util.List;
import java.util.Optional;

import com.acm.server.domain.AcademicClub;
import com.acm.server.domain.CentralClub;
import com.acm.server.domain.Club;

public interface FindClubPort {
    List<Club> findAllClub();
    Optional<CentralClub> findCentralClub(Long clubId);
    Optional<AcademicClub> findAcademicClub(Long clubId);
    List<Club> findFilterClub(String type, String category, Boolean isRecruiting, String department);
    Optional<Club> findClub(Long ClubId);
}
