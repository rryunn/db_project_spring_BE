package com.acm.server.application.club.port.out;

import java.util.List;

import com.acm.server.domain.AcademicClub;
import com.acm.server.domain.CentralClub;
import com.acm.server.domain.Club;

public interface FindClubPort {
    public List<Club> findAllClub();
    public CentralClub findCentralClub(Long clubId);
    public AcademicClub findAcademicClub(Long clubId);
    public List<Club> findFilterClub(String type, String category,Boolean isRecruiting, String department);
}
