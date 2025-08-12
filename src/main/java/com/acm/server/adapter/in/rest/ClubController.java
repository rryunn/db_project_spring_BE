package com.acm.server.adapter.in.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.acm.server.adapter.in.response.Response;
import com.acm.server.application.club.port.in.FindClubUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/club")
@RequiredArgsConstructor
public class ClubController {
    private final FindClubUseCase findClubUseCase;

    @GetMapping("/all")
    public Response getAllClub(){
        return findClubUseCase.findAllClub();
    }
}

    // @GetMapping("/user/{playerid}")
    // public List<GameResultResponse> getGameResult(@PathVariable Long playerid) {
    //     // Get all game results for a user
    //     return gameService.findByPlayerId(playerid);
    // }