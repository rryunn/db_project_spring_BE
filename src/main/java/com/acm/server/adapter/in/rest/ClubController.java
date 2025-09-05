package com.acm.server.adapter.in.rest;

import org.springframework.web.bind.annotation.*;
import com.acm.server.adapter.in.response.Response;
import com.acm.server.application.club.port.in.FindClubUseCase;
import com.acm.server.application.clubimage.port.in.FindClubActivityImagesUseCase;

import lombok.RequiredArgsConstructor;

// Swagger / springdoc
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/club")
@RequiredArgsConstructor
@Tag(name = "Club", description = "동아리(중앙/소학회) 조회 및 검색 API")
@SecurityRequirement(name = "BearerAuth") // Swagger UI에서 JWT 넣고 테스트하려면 필요
public class ClubController {

    private final FindClubUseCase findClubUseCase;
    private final FindClubActivityImagesUseCase findClubActivityImagesUseCase;

    @Operation(summary = "전체 동아리 목록", description = "모든 동아리(중앙/소학회 포함)를 반환합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content),
        @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping("/all")
    public Response getAllClub() {
        var data = findClubUseCase.findAllClub();
        return new Response(200, "success", data);
    }

    @Operation(summary = "중앙동아리 상세", description = "clubId로 중앙동아리 상세 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "동아리 없음", content = @Content)
    })
    @GetMapping("/central/{clubId}")
    public Response getCentralClub(
            @Parameter(description = "중앙동아리 ID", example = "1")
            @PathVariable Long clubId) {
        var data = findClubUseCase.findCentralClub(clubId);
        return new Response(200, "success", data);
    }

    @Operation(summary = "소학회 상세", description = "clubId로 소학회 상세 정보를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "동아리 없음", content = @Content)
    })
    @GetMapping("/academic/{clubId}")
    public Response getAcademicClub(
            @Parameter(description = "소학회 ID", example = "10")
            @PathVariable Long clubId) {
        var data = findClubUseCase.findAcademicClub(clubId);
        return new Response(200, "success", data);
    }

    @Operation(
        summary = "동아리 필터 검색",
        description = """
        조건(type, category, isRecruiting, department)으로 동아리를 검색합니다.
        - type: 중앙동아리 | 소학회
        - category: 스포츠 | 학술 | 종교 | 문화/예술 | 창업 | 사교 | 봉사
        - isRecruiting: true/false
        - department: 학과/대학 명 (소학회 필터에 유용)
        예) /api/club/filter?type=소학회&category=학술&isRecruiting=true&department=소프트웨어학과
        """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "400", description = "요청 파라미터 오류", content = @Content)
    })
    @Parameters({
        @Parameter(
            name = "type",
            description = "동아리 유형",
            schema = @Schema(allowableValues = {"중앙동아리", "소학회"})
        ),
        @Parameter(
            name = "category",
            description = "동아리 카테고리",
            schema = @Schema(allowableValues = {"스포츠", "학술", "종교", "문화/예술", "창업", "사교", "봉사"})
        ),
        @Parameter(
            name = "isRecruiting",
            description = "모집 여부",
            schema = @Schema(type = "boolean"),
            example = "true"
        ),
        @Parameter(
            name = "department",
            description = "학과/대학 명 (소학회에 유용)",
            schema = @Schema(allowableValues = {
                "소프트웨어융합학과","디지털미디어학과","국방디지털융합학과","사이버보안학과","소프트웨어학과",
                "기계공학과","산업공학과","화학공학과","첨단신소재공학과","응용화학생명공학과","미래모빌리티공학과",
                "환경안전공학과","건설시스템공학과","교통시스템공학과","건축학과","융합시스템공학과",
                "응용화학과","전자공학과","지능형반도체공학과","수학과","물리학과","프런티어과학학부","화학과",
                "생명과학과","경영학과","경영인텔리전스학과","금융공학과","글로벌경영학과","국어국문학과",
                "영어영문학과","불어불문학과","사학과","문화콘텐츠학과","경제학과","행정학과","심리학과",
                "경제정치사회융합학부","사회학과","정치외교학과","스포츠레저학과","의학과","간호학과","약학과",
                "첨단바이오융합대학","다산학부대학","국제학부","소프트웨어융합대학","인공지능융합학과",
                "경영대학","인문대학","자연과학대학"
            })
        )
    })
    @GetMapping("/filter")
    public Response filterClubs(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean isRecruiting,
            @RequestParam(required = false) String department) {

        var data = findClubUseCase.findFilterClub(type, category, isRecruiting, department);
        return new Response(200, "success", data);
    }

    @Operation(
    summary = "클럽 활동사진 조회",
    description = "clubId로 해당 클럽의 활동사진 리스트를 반환합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "해당 클럽 없음", content = @Content)
    })
    @GetMapping("/{clubId}/activity-images")
    public Response getClubActivityImages(
            @Parameter(description = "클럽 ID", example = "1")
            @PathVariable Long clubId) {

        var data = findClubActivityImagesUseCase.findByClubId(clubId);
        return new Response(200, "success", data);
    }
}
