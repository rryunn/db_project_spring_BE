package com.acm.server.domain;

import com.acm.server.adapter.out.entity.RecruitmentType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class Recruitment {
    private final Long id;
    private final Long clubId;
    private final String clubName;

    private final String title;
    private final String description;
    private final RecruitmentType type;

    private final String phoneNumber;
    private final String email;

    private final LocalDate startDate;
    private final LocalDate endDate;

    private final String url;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
