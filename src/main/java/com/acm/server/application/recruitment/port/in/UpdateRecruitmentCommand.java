package com.acm.server.application.recruitment.port.in;

import com.acm.server.adapter.out.entity.RecruitmentType;
import java.time.LocalDate;

public record UpdateRecruitmentCommand(
        Long recruitmentId,
        Long clubId,
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
