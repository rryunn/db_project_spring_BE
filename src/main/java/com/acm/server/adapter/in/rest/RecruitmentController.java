package com.acm.server.adapter.in.rest;

import com.acm.server.adapter.in.dto.RecruitmentRequest;
import com.acm.server.adapter.in.response.Response;
import com.acm.server.adapter.in.security.JwtUserPrincipal;
import com.acm.server.application.recruitment.dto.UpdateRecruitmentReq;
import com.acm.server.application.recruitment.port.in.CreateRecruitmentUseCase;
import com.acm.server.application.recruitment.port.in.FindRecruitmentUseCase;
import com.acm.server.application.recruitment.port.in.ManageRecruitmentImagesUseCase;
import com.acm.server.application.recruitment.port.in.UpdateRecruitmentUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/recruitments")
@RequiredArgsConstructor
public class RecruitmentController {

    private final FindRecruitmentUseCase findRecruitmentUseCase;
    private final CreateRecruitmentUseCase createRecruitmentUseCase;
    private final UpdateRecruitmentUseCase updateRecruitmentUseCase;
    private final ManageRecruitmentImagesUseCase manageRecruitmentImagesUseCase;

    @GetMapping
    public Response getRecruitments() {
        var data = findRecruitmentUseCase.findAllRecruitment(); // 그냥 domain 반환
        return new Response(200, "success", data);
    }

    @Operation(summary = "클럽 ID로 모집공고 목록 조회", description = "특정 clubId에 속한 모든 모집공고 목록을 반환합니다.")
    @GetMapping("/{clubId}")
    public Response getRecruitmentsByClubId(@PathVariable Long clubId) {
        var data = findRecruitmentUseCase.findRecruitmentsByClubId(clubId);
        return new Response(200, "success", data);
    }

    @Operation(summary = "모집공고 ID로 모집공고 상세 조회")
    @GetMapping("/{recruitmentId}")
    public Response getRecruitmentById(@PathVariable Long recruitmentId, @AuthenticationPrincipal JwtUserPrincipal principal) {
        var data = findRecruitmentUseCase.findRecruitmentByIdWithView(recruitmentId, principal.userId())
                    .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "조회하려는 모집공고가 존재하지 않습니다. 모집공고 아이디: " + recruitmentId
                ));

        return new Response(200, "success", data);
    }

    @GetMapping("/main")
    public Response getMainRecruitment() {
        var data = findRecruitmentUseCase.getMainRecruitment();
        return new Response(200, "success", data);
    }
    
    @Operation(
            summary = "모집공고 삭제",
            description = "특정 클럽(clubId)의 모집공고를 삭제합니다. \n\n" +
                    "인증된 사용자 중 해당 클럽의 관리자만 이 작업을 수행할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공 (No Content)"),
            @ApiResponse(responseCode = "403", description = "권한 없음 (해당 클럽의 관리자가 아님)", content = @Content),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 clubId 또는 모집공고", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @SecurityRequirement(name = "BearerAuth")
    @DeleteMapping("/{recruitmentId}")
    public void deleteRecruitmentById(@PathVariable Long recruitmentId, @AuthenticationPrincipal JwtUserPrincipal principal) {
        var recruitment = findRecruitmentUseCase.findRecruitmentById(recruitmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "모집공고를 찾을 수 없습니다."));

        Long clubId = recruitment.getClubId();
        if (principal.managedClubs() == null || !principal.managedClubs().contains(clubId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
        findRecruitmentUseCase.deleteRecruitmentById(recruitmentId);
    }

    @Operation(
            summary = "모집공고 생성",
            description = "특정 클럽(clubId)에 대한 새 모집공고를 생성합니다. 인증된 사용자 중 해당 클럽의 관리자만 이 작업을 수행할 수 있습니다. \n\n" +
                    "전화번호: 010-1234-5678, 날짜: YYYY-MM-DD, type: [상시모집|수시모집], 선택 필드는 null 또는 생략 가능"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "모집공고 생성 성공"),
            @ApiResponse(responseCode = "400", description = "요청 본문 유효성 검사 실패 (예: 날짜 형식 오류, 필수 필드 누락)", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음 (해당 클럽의 관리자가 아님)", content = @Content),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 clubId", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("/{clubId}")
    public Response createRecruitment(
            @PathVariable Long clubId,
            @RequestBody @Valid RecruitmentRequest req,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        if (principal.managedClubs() == null || !principal.managedClubs().contains(clubId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
        var created = createRecruitmentUseCase.createRecruitment(req.toCommand(clubId));
        return new Response(201,"created", created);
    }
    @Operation(
            summary = "모집공고 부분 수정 (PATCH)",
            description = "기존 모집공고의 내용을 부분 수정합니다. 요청 본문에 포함된 필드만 업데이트됩니다 (PATCH). \n\n" +
                    "인증된 사용자 중 해당 클럽의 관리자만 이 작업을 수행할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "요청 본문 유효성 검사 실패", content = @Content),
            @ApiResponse(responseCode = "403", description = "권한 없음 (해당 클럽의 관리자가 아님)", content = @Content),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 clubId 또는 모집공고", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @SecurityRequirement(name = "BearerAuth")
    @PatchMapping("/{recruitmentId}")
    public Response updateRecruitment(@PathVariable Long recruitmentId, @RequestBody @Valid UpdateRecruitmentReq req,@AuthenticationPrincipal JwtUserPrincipal principal) {
        var recruitment = findRecruitmentUseCase.findRecruitmentById(recruitmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "모집공고를 찾을 수 없습니다."));

        Long clubId = recruitment.getClubId(); // 공고에서 clubId 추출

        if (principal.managedClubs() == null || !principal.managedClubs().contains(clubId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "권한이 없습니다.");
        }
        var updated = updateRecruitmentUseCase.updateRecruitment(req.toCommand(recruitmentId));
        return new Response(200, "success", updated);
    }
    @GetMapping("/{id}/images")
    @Operation(summary = "모집공고 이미지 조회", description = "모집공고 id를 이용해 해당 공고에 등록된 이미지 URL 리스트를 조회합니다.")
    public ResponseEntity<List<String>> getRecruitmentImages(@PathVariable("id") Long recruitmentId) {
        List<String> imageUrls = findRecruitmentUseCase.getRecruitmentImageUrls(recruitmentId);
        return ResponseEntity.ok(imageUrls);
    }

    @Operation(
            summary = "모집공고 이미지 업로드 (여러 장)",
            description = "해당 모집공고에 이미지를 업로드합니다. (관리자 권한 필요)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업로드 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "모집공고 없음", content = @Content)
    })
    @SecurityRequirement(name = "BearerAuth")
    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response uploadRecruitmentImages(
            @PathVariable("id") Long recruitmentId,
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @RequestPart("files") List<MultipartFile> files
    ) {
        // 권한 검사는 서비스 레이어(useCase)에서 수행
        var data = manageRecruitmentImagesUseCase.upload(principal, recruitmentId, files);
        return new Response(200, "success", data);
    }

    @Operation(
            summary = "모집공고 이미지 교체 (1장)",
            description = "oldUrl에 해당하는 이미지를 새 파일로 교체합니다. (관리자 권한 필요)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "교체 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "모집공고 또는 이미지 없음", content = @Content)
    })
    @SecurityRequirement(name = "BearerAuth")
    // PUT 또는 POST (ClubController가 PUT을 사용했으므로 PUT으로 통일)
    @PutMapping(value = "/{id}/images/by-url", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response replaceRecruitmentImageByUrl(
            @PathVariable("id") Long recruitmentId,
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @RequestParam("oldUrl") String oldUrl,
            @RequestPart("file") MultipartFile file
    ) {
        var data = manageRecruitmentImagesUseCase.replaceByUrl(principal, recruitmentId, oldUrl, file);
        return new Response(200, "success", data);
    }

    @Operation(
            summary = "모집공고 이미지 삭제 (1장)",
            description = "url로 지정된 사진 1장을 삭제합니다. (관리자 권한 필요)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "모집공고 또는 이미지 없음", content = @Content)
    })
    @SecurityRequirement(name = "BearerAuth")
    @DeleteMapping("/{id}/images/one")
    public Response deleteOneRecruitmentImage(
            @PathVariable("id") Long recruitmentId,
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @RequestParam("url") String url
    ) {
        manageRecruitmentImagesUseCase.deleteOneByUrl(principal, recruitmentId, url);
        return new Response(200, "success", Map.of("deleted", true));
    }

    @Operation(
            summary = "모집공고 이미지 전체 삭제",
            description = "해당 모집공고의 모든 이미지를 삭제합니다. (관리자 권한 필요)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "권한 없음", content = @Content),
            @ApiResponse(responseCode = "404", description = "모집공고 없음", content = @Content)
    })
    @SecurityRequirement(name = "BearerAuth")
    @DeleteMapping("/{id}/images/all")
    public Response deleteAllRecruitmentImages(
            @PathVariable("id") Long recruitmentId,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        manageRecruitmentImagesUseCase.deleteAll(principal, recruitmentId);
        return new Response(200, "success", Map.of("deletedAll", true));
    }
}
