package com.acm.server.application.favorite.port.out;

public interface DeleteRecruitmentFavoritePort {
    void deleteFavorite(Long userId, Long recruitmentId);
}
