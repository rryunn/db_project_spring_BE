package com.acm.server.application.favorite.port.out;

public interface SaveRecruitmentFavoritePort {
    void saveFavorite(Long userId, Long recruitmentId);
}
