package com.acm.server.domain;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AcademicClub {
    private Long id;
    private String name;
    private String description;
    private String mainActivities;
    private String location;
    private String contactPhoneNumber;
    private String instagramUrl;
    private String youtubeUrl;
    private String linktreeUrl;
    private String clubUrl;
    private String contactEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String clubType;
    private String logoUrl;
    private String category;
    private boolean isRecruiting;

    private String recruitmentScope;
    private String departmentName;
}
