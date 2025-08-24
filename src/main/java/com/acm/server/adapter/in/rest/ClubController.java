package com.acm.server.adapter.in.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.acm.server.adapter.in.response.Response;
import com.acm.server.application.club.port.in.FindClubUseCase;
import com.acm.server.domain.Club;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/club")
@RequiredArgsConstructor
public class ClubController {
    private final FindClubUseCase findClubUseCase;

    @GetMapping("/all")
    public Response getAllClub(){
        var data = findClubUseCase.findAllClub(); // 그냥 domain 반환
        return new Response(200, "success", data);
    }

    @GetMapping("/central/{clubId}")
    public Response getCentralClub(@PathVariable Long clubId) {
        var data = findClubUseCase.findCentralClub(clubId);
        return new Response(200, "success", data);
    }

    @GetMapping("/academic/{clubId}")
    public Response getAcademicClub(@PathVariable Long clubId) {
        var data = findClubUseCase.findAcademicClub(clubId);
        return new Response(200, "success", data);
    }
    @GetMapping("/filter")
    public Response filterClubs(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean isRecruiting,
            @RequestParam(required = false) String department) {

        var data =  findClubUseCase.findFilterClub(type, category, isRecruiting, department);
        return new Response(200, "success", data);
    }
}

    // @GetMapping("/user/{playerid}")
    // public List<GameResultResponse> getGameResult(@PathVariable Long playerid) {
    //     // Get all game results for a user
    //     return gameService.findByPlayerId(playerid);
    // }