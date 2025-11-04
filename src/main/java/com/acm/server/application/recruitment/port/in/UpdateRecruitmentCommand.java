package com.acm.server.application.recruitment.port.in;

import com.acm.server.adapter.out.entity.RecruitmentType;
import java.time.LocalDate;

public record UpdateRecruitmentCommand(
        Long clubId, // 어떤 클럽의 공고를 수정할지 식별
        String title,
        String description,
        RecruitmentType type,
        String phoneNumber,
        String email,
        LocalDate startDate,
        LocalDate endDate,
        String url
) {
}
