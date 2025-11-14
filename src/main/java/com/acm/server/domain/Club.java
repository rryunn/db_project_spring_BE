package com.acm.server.domain;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Club {

    private Long id;
    private String name;
    private String description;
    private String mainActivities;
    private String location;
    
    private String instagramUrl;
    private String youtubeUrl;
    private String linktreeUrl;
    private String clubUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String clubType;
    private String logoUrl;
    private String category;
    private boolean isRecruiting;
}
