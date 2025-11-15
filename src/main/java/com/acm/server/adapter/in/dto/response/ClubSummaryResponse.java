package com.acm.server.adapter.in.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ClubSummaryResponse {

    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String clubType;    // "중앙동아리" 등
    private String logoUrl;
    private String category;    // ACADEMIC, CULTURE_ART 등
    private boolean recruiting; // 상시모집 여부
}
