package com.acm.server.application.favorite;

import lombok.Builder;

@Builder
public record RecruitmentFavoriteView(
        Long clubId,
        Long recruitmentId,
        String title,
        String thumbnailUrl
) {}
