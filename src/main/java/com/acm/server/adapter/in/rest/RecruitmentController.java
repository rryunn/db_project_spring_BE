package com.acm.server.adapter.in.rest;

import com.acm.server.adapter.in.response.Response;
import com.acm.server.application.recruitment.port.in.FindRecruitmentUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recruitments")
@RequiredArgsConstructor
public class RecruitmentController {

    private final FindRecruitmentUseCase findRecruitmentUseCase;

    @GetMapping
    public Response getRecruitments() {
        var data = findRecruitmentUseCase.findAllRecruitment(); // 그냥 domain 반환
        return new Response(200, "success", data);
    }

    @GetMapping("/{recruitments_id}")
    public Response getRecruitmentById(@PathVariable Long id) {
        var data = findRecruitmentUseCase.findRecruitmentById(id);
        return new Response(200, "success", data);
    }
    @DeleteMapping("/{recruitments_id}")
    public void deleteRecruitmentById(@PathVariable Long id) {
        findRecruitmentUseCase.deleteRecruitmentById(id);
    }
}
