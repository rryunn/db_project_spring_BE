package com.acm.server.adapter.in.rest;

import com.acm.server.adapter.in.dto.RecruitmentRequest;
import com.acm.server.adapter.in.response.Response;
import com.acm.server.application.recruitment.port.in.CreateRecruitmentUseCase;
import com.acm.server.application.recruitment.port.in.FindRecruitmentUseCase;
import com.acm.server.application.recruitment.port.in.RecruitmentCommand;
import com.acm.server.application.recruitment.port.in.UpdateRecruitmentUseCase;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jdk.jfr.Description;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruitments")
@RequiredArgsConstructor
public class RecruitmentController {

    private final FindRecruitmentUseCase findRecruitmentUseCase;
    private final CreateRecruitmentUseCase createRecruitmentUseCase;
    private final UpdateRecruitmentUseCase updateRecruitmentUseCase;
    @GetMapping
    public Response getRecruitments() {
        var data = findRecruitmentUseCase.findAllRecruitment(); // 그냥 domain 반환
        return new Response(200, "success", data);
    }

    @GetMapping("/{clubId}")
    public Response getRecruitmentByClubId(@PathVariable Long clubId) {
        var data = findRecruitmentUseCase.findRecruitmentByClubId(clubId);
        return new Response(200, "success", data);
    }
    @GetMapping("/main")
    public Response getMainRecruitment() {
        var data = findRecruitmentUseCase.getMainRecruitment();
        return new Response(200, "success", data);
    }
    @GetMapping("/{id}/images")
    @Operation(summary = "모집공고 이미지 조회", description = "모집공고 id를 이용해 해당 공고에 등록된 이미지 URL 리스트를 조회합니다.")
    public ResponseEntity<List<String>> getRecruitmentImages(@PathVariable Long id) {
        List<String> imageUrls = findRecruitmentUseCase.getRecruitmentImageUrls(id);
        return ResponseEntity.ok(imageUrls);
    }
    @DeleteMapping("/{clubId}")
    public void deleteRecruitmentById(@PathVariable Long clubId) {
        findRecruitmentUseCase.deleteRecruitmentById(clubId);
    }

    @PostMapping("/{clubId}")
    @Operation(description = "전화번호: 010-1234-5678, 날짜: YYYY-MM-DD, type: [상시모집|수시모집], 선택 필드는 null 또는 생략 가능")
    public Response createRecruitment(
            @PathVariable Long clubId,
            @RequestBody @Valid RecruitmentRequest req
    ) {
        var created = createRecruitmentUseCase.createRecruitment(req.toCommand(clubId));
        return new Response(201,"created", created);
    }

    @PutMapping("/{clubId}")
    public Response updateRecruitment(@PathVariable Long clubId, @RequestBody @Valid RecruitmentRequest req) {
        var updated = updateRecruitmentUseCase.updateRecruitment(req.toCommand(clubId));
        return new Response(200, "success", updated);
    }
}
