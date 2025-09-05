package com.acm.server.adapter.in.rest;

import com.acm.server.adapter.in.security.JwtUserPrincipal;
import com.acm.server.application.favorite.port.in.RecruitmentFavoriteUseCase;
import com.acm.server.application.favorite.RecruitmentFavoriteView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruitments/favorites")
@RequiredArgsConstructor
public class RecruitmentFavoriteController {

    private final RecruitmentFavoriteUseCase useCase;

    @Operation(summary = "즐겨찾기 추가", description = "현재 로그인한 사용자가 지정한 모집공고를 즐겨찾기에 추가합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "즐겨찾기 추가 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "해당 모집공고를 찾을 수 없음")
    })
    @PostMapping("/{recruitmentId}")
    public void addFavorite(
            @Parameter(hidden = true) @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(description = "즐겨찾기할 모집공고 ID", example = "101")
            @PathVariable Long recruitmentId) {
        useCase.addFavorite(principal.userId(), recruitmentId);
    }

    @Operation(summary = "즐겨찾기 삭제", description = "현재 로그인한 사용자가 지정한 모집공고의 즐겨찾기를 해제합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "즐겨찾기 삭제 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "404", description = "즐겨찾기 기록을 찾을 수 없음")
    })
    @DeleteMapping("/{recruitmentId}")
    public void removeFavorite(
            @Parameter(hidden = true) @AuthenticationPrincipal JwtUserPrincipal principal,
            @Parameter(description = "즐겨찾기 해제할 모집공고 ID", example = "101")
            @PathVariable Long recruitmentId) {
        useCase.removeFavorite(principal.userId(), recruitmentId);
    }

    @Operation(summary = "내 즐겨찾기 목록 조회", description = "현재 로그인한 사용자의 즐겨찾기 목록을 반환합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "즐겨찾기 목록 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping
    public List<RecruitmentFavoriteView> getMyFavorites(
            @Parameter(hidden = true) @AuthenticationPrincipal JwtUserPrincipal principal) {
        return useCase.getMyFavorites(principal.userId());
    }
}
