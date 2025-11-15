package com.acm.server.adapter.in.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class RecruitmentSummaryResponse {

    private Long id;
    private Long clubId;
    private String clubName;
    private String title;
    private String type;          // "상시모집" 같은 값
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long viewCount;
    private Long saveCount;
    private String semester;
}