package com.acm.server.adapter.in.rest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.acm.server.adapter.in.response.Response;
import com.acm.server.adapter.in.security.JwtUserPrincipal;
import com.acm.server.application.club.dto.UpdateClubReq;
import com.acm.server.application.club.port.in.FindClubUseCase;
import com.acm.server.application.club.port.in.ManageClubActivityImagesUseCase;
import com.acm.server.application.clubimage.port.in.FindClubActivityImagesUseCase;
import com.acm.server.application.club.port.in.UpdateClubUseCase;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.acm.server.application.club.port.in.UpdateClubLogoUseCase;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import java.time.ZoneId;
import java.util.Map;

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
    private final UpdateClubUseCase updateClubUseCase;
    private final UpdateClubLogoUseCase updateClubLogoUseCase;
    private final ManageClubActivityImagesUseCase manageClubActivityImagesUseCase;
    
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
        summary = "클럽 정보 부분 수정",
        description = """
            관리 권한이 있는 클럽의 일부 정보(description, mainActivities, location, sns1~4)만 수정할 수 있습니다.
            상위 category, 이름, 모집 여부 등은 수정 불가합니다.

            ✅ 예시 요청 바디:
            ```json
            {
            "description": "새로운 클럽 소개입니다.",
            "mainActivities": "정기공연, 대동제 참가",
            "location": "학생회관 3층 밴드실",
            "instagramUrl": "https://instagram.com/rockers_ajou",
            "youtubeUrl": "https://youtube.com/@rockersband",
            "linktreeUrl": "https://linktr.ee/rockers",
            "clubUrl": "https://rockers.ajou.ac.kr"
            }
            ```
        """
    )
    @PatchMapping("/{clubId}")
    public Response patchClub(
            @PathVariable Long clubId,
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "수정할 필드만 포함하면 됩니다 (null 또는 누락된 필드는 유지됩니다).",
                    required = true,
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UpdateClubReq.class)
                    )
            )
            @RequestBody UpdateClubReq req
    ) {
        if (principal.managedClubs() == null || !principal.managedClubs().contains(clubId)) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
        var data = updateClubUseCase.updateClub(clubId, req);
        return new Response(200, "success", data);
    }



    @Operation(
        summary = "클럽 로고 업로드/교체",
        description = "관리 권한이 있는 사용자가 클럽 로고 이미지를 업로드합니다. 업로드가 성공하면 이전 로고 파일을 삭제하고 DB의 logoUrl을 새 URL로 교체합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "업로드 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 파일", content = @Content),
        @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
        @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @SecurityRequirement(name = "BearerAuth")
    @PutMapping(value = "/{clubId}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response uploadOrReplaceClubLogo(
            @PathVariable Long clubId,
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @RequestPart("file") MultipartFile file
    ) {
        // 권한 검사
        if (principal.managedClubs() == null || !principal.managedClubs().contains(clubId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }

        var club = updateClubLogoUseCase.updateLogo(clubId, file);

        var data = Map.of(
            "clubId", club.getId(),
            "logoUrl", club.getLogoUrl()
        );
        return new Response(200, "success", data);
    }

    ///////////////////////////////클럽 활동사진///////////////////////////////////////////////////////

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

    @Operation(
        summary = "클럽 활동사진 업로드(여러 장 가능)",
        description = "처음 업로드든 추가 업로드든 모두 이 엔드포인트를 사용합니다. 업로드 순서 = 노출 순서(PK 오름차순)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "업로드 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 파일", content = @Content),
        @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @PostMapping(value = "/{clubId}/activity-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response uploadClubActivityImages(
            @PathVariable Long clubId,
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @RequestPart("files") java.util.List<MultipartFile> files
    ) {
        if (principal.managedClubs() == null || !principal.managedClubs().contains(clubId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
        var data = manageClubActivityImagesUseCase.upload(clubId, files); // 반환: [{id, url}, ...]
        return new Response(200, "success", data);
    }


    @Operation(
        summary = "클럽 활동사진 교체(한 장)",
        description = "oldUrl에 해당하는 사진 1장을 새 파일로 교체합니다. 순서는 유지됩니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "교체 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
        @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
        @ApiResponse(responseCode = "404", description = "해당 이미지 없음", content = @Content)
    })
    @PutMapping(value = "/{clubId}/activity-images/by-url", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response replaceClubActivityImageByUrl(
            @PathVariable Long clubId,
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @RequestParam("oldUrl") String oldUrl,
            @RequestPart("file") MultipartFile file
    ) {
        if (principal.managedClubs() == null || !principal.managedClubs().contains(clubId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
        var data = manageClubActivityImagesUseCase.replaceByUrl(clubId, oldUrl, file); // 반환: {id, url}
        return new Response(200, "success", data);
    }

    @Operation(
        summary = "클럽 활동사진 삭제(한 장)",
        description = "url로 지정된 사진 1장을 삭제합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "삭제 성공"),
        @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
        @ApiResponse(responseCode = "404", description = "해당 이미지 없음", content = @Content)
    })
    @DeleteMapping("/{clubId}/activity-images/one")
    public Response deleteOneClubActivityImage(
            @PathVariable Long clubId,
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @RequestParam("url") String url
    ) {
        if (principal.managedClubs() == null || !principal.managedClubs().contains(clubId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
        manageClubActivityImagesUseCase.deleteOneByUrl(clubId, url);
        return new Response(200, "success", Map.of("deleted", true));
    }

    @Operation(
        summary = "클럽 활동사진 전부 삭제",
        description = "해당 클럽의 모든 활동사진을 삭제합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "전체 삭제 성공"),
        @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content)
    })
    @DeleteMapping("/{clubId}/activity-images/all")
    public Response deleteAllClubActivityImages(
            @PathVariable Long clubId,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        if (principal.managedClubs() == null || !principal.managedClubs().contains(clubId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
        manageClubActivityImagesUseCase.deleteAll(clubId);
        return new Response(200, "success", Map.of("deletedAll", true));
    }

}
