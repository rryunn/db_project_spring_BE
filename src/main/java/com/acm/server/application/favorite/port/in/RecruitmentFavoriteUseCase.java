package com.acm.server.application.favorite.port.in;

import com.acm.server.application.favorite.RecruitmentFavoriteView;

import java.util.List;

public interface RecruitmentFavoriteUseCase {
    void addFavorite(Long userId, Long recruitmentId);
    void removeFavorite(Long userId, Long recruitmentId);
    List<RecruitmentFavoriteView> getMyFavorites(Long userId);
}