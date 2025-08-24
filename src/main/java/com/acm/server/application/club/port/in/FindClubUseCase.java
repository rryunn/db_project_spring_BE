package com.acm.server.application.club.port.in;

import java.util.List;

import com.acm.server.adapter.in.dto.FindClubDto;
import com.acm.server.adapter.in.response.Response;
import com.acm.server.domain.AcademicClub;
import com.acm.server.domain.CentralClub;
import com.acm.server.domain.Club;

public interface FindClubUseCase {
    public List<Club> findAllClub();
    public AcademicClub findAcademicClub(Long clubId);
    public CentralClub findCentralClub(Long clubId);
    public List<Club> findFilterClub(String type, String category,Boolean isRecruiting, String department);
}