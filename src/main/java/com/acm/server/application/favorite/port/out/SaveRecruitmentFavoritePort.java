package com.acm.server.application.favorite.port.out;

public interface SaveRecruitmentFavoritePort {
    boolean saveFavorite(Long userId, Long recruitmentId);
}
