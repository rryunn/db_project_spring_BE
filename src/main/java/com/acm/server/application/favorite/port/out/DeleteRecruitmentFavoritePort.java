package com.acm.server.application.favorite.port.out;

public interface DeleteRecruitmentFavoritePort {
    boolean deleteFavorite(Long userId, Long recruitmentId);
}
