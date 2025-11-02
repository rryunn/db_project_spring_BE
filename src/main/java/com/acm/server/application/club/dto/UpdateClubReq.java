package com.acm.server.application.club.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateClubReq {
    private String description;    // 소개
    private String mainActivities; // 주요 활동
    private String location;       // 과방
    private String instagramUrl;
    private String youtubeUrl;
    private String linktreeUrl;
    private String clubUrl;
}
