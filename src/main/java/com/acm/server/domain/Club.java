package com.acm.server.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Club {
    private Long id;
    private String name;
    private String description;
    private String location;
    private String getMainActivities;
    private String clubType;
    private String logoUrl;
    private String category;
    private String instagramUrl;
    private String youtubeUrl;
    private String linktreeUrl;
    private String clubUrl;
    private boolean isRecruiting;
}
