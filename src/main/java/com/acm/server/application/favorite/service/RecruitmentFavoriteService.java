package com.acm.server.application.favorite.service;

import com.acm.server.application.favorite.port.in.RecruitmentFavoriteUseCase;
import com.acm.server.application.favorite.port.out.DeleteRecruitmentFavoritePort;
import com.acm.server.application.favorite.port.out.LoadRecruitmentFavoritesPort;
import com.acm.server.application.favorite.port.out.SaveRecruitmentFavoritePort;
import com.acm.server.application.recruitment.port.out.FindRecruitmentPort;
import com.acm.server.common.exception.ResourceNotFoundException;
import com.acm.server.application.favorite.RecruitmentFavoriteView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecruitmentFavoriteService implements RecruitmentFavoriteUseCase {

    private final SaveRecruitmentFavoritePort savePort;
    private final DeleteRecruitmentFavoritePort deletePort;
    private final LoadRecruitmentFavoritesPort loadPort;
    private final FindRecruitmentPort findRecruitmentPort;

    @Override
    public void addFavorite(Long userId, Long recruitmentId) {
        findRecruitmentPort.findRecruitmentById(recruitmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Recruitment not found: " + recruitmentId));
        savePort.saveFavorite(userId, recruitmentId);
    }

    @Override
    public void removeFavorite(Long userId, Long recruitmentId) {
        findRecruitmentPort.findRecruitmentById(recruitmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Recruitment not found: " + recruitmentId));
        deletePort.deleteFavorite(userId, recruitmentId);
    }

    @Override
    public List<RecruitmentFavoriteView> getMyFavorites(Long userId) {
        return loadPort.findFavoritesByUserId(userId);
    }
}

