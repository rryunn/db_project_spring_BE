package com.acm.server.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecruitmentImage {
    private final Long id;
    private final Long recruitmentId;
    private final String imageUrl;

}
