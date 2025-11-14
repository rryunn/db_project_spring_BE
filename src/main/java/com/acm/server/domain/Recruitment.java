package com.acm.server.domain;

import com.acm.server.adapter.out.entity.RecruitmentType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
public class Recruitment {
    private Long id;
    private Long clubId;
    private String clubName;

    private String title;
    private String description;
    private RecruitmentType type;

    private String phoneNumber;
    private String email;

    private LocalDate startDate;
    private LocalDate endDate;

    private String url;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long viewCount;
    private Long saveCount; 

    private String semester;
}
