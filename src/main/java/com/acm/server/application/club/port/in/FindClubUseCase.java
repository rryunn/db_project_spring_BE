package com.acm.server.application.club.port.in;

import com.acm.server.adapter.in.dto.FindClubDto;
import com.acm.server.adapter.in.response.Response;

public interface FindClubUseCase {
    public Response findAllClub();
    public Response findAcademicClub(FindClubDto findClubDto);
    public Response findCentralClub(FindClubDto findClubDto);
    public Response findCategoryClub(FindClubDto findClubDto);
    public Response findClub(FindClubDto findClubDto);
}
