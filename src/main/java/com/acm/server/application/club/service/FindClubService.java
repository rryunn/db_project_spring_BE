package com.acm.server.application.club.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.acm.server.adapter.in.dto.FindClubDto;
import com.acm.server.adapter.in.response.Response;
import com.acm.server.application.club.port.in.FindClubUseCase;
import com.acm.server.application.club.port.out.FindClubPort;
import com.acm.server.domain.Club;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FindClubService implements FindClubUseCase{

    private final FindClubPort findClubPort;

    @Override
    public Response findAllClub() {
        List<Club> clubList = findClubPort.findAllClub();
        return new Response(200, "Success", clubList);
    }

    @Override
    public Response findAcademicClub(FindClubDto findClubDto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAcademicClub'");
    }

    @Override
    public Response findCentralClub(FindClubDto findClubDto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findCentralClub'");
    }

    @Override
    public Response findCategoryClub(FindClubDto findClubDto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findCategoryClub'");
    }

    @Override
    public Response findClub(FindClubDto findClubDto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findClub'");
    }
    
}
