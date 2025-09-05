package com.acm.server.application.favorite.port.out;

import com.acm.server.application.favorite.RecruitmentFavoriteView;

import java.util.List;

public interface LoadRecruitmentFavoritesPort {
    List<RecruitmentFavoriteView> findFavoritesByUserId(Long userId);
}
