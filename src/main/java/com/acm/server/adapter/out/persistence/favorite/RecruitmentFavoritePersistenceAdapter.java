package com.acm.server.adapter.out.persistence.favorite;

import com.acm.server.adapter.out.entity.RecruitmentEntity;
import com.acm.server.adapter.out.entity.RecruitmentFavoriteEntity;
import com.acm.server.adapter.out.entity.RecruitmentFavoriteId;
import com.acm.server.adapter.out.entity.RecruitmentImage;
import com.acm.server.adapter.out.entity.UserEntity;
import com.acm.server.adapter.out.persistence.favorite.JpaRecruitmentFavoriteRepository;
import com.acm.server.adapter.out.persistence.recruitment.JpaRecruitmentImageRepository;
import com.acm.server.adapter.out.persistence.recruitment.JpaRecruitmentRepository;
import com.acm.server.adapter.out.persistence.user.JpaUserRepository;
import com.acm.server.application.favorite.port.out.DeleteRecruitmentFavoritePort;
import com.acm.server.application.favorite.port.out.LoadRecruitmentFavoritesPort;
import com.acm.server.application.favorite.port.out.SaveRecruitmentFavoritePort;
import com.acm.server.application.favorite.RecruitmentFavoriteView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecruitmentFavoritePersistenceAdapter
        implements SaveRecruitmentFavoritePort, DeleteRecruitmentFavoritePort, LoadRecruitmentFavoritesPort {

    private final JpaRecruitmentFavoriteRepository favoriteRepo;
    private final JpaUserRepository userRepo;
    private final JpaRecruitmentRepository recruitmentRepo;
    private final JpaRecruitmentImageRepository imageRepo;

    @Override
    public void saveFavorite(Long userId, Long recruitmentId) {
        UserEntity userRef = userRepo.getReferenceById(userId);                  // SELECT 없이 프록시
        RecruitmentEntity recRef = recruitmentRepo.getReferenceById(recruitmentId);

        var entity = RecruitmentFavoriteEntity.builder()
                .id(new RecruitmentFavoriteId(userId, recruitmentId))
                .user(userRef)
                .recruitment(recRef)
                .build();

        favoriteRepo.save(entity);
    }

    @Override
    public void deleteFavorite(Long userId, Long recruitmentId) {
        favoriteRepo.deleteById(new RecruitmentFavoriteId(userId, recruitmentId));
    }

    @Override
    public List<RecruitmentFavoriteView> findFavoritesByUserId(Long userId) {
        return favoriteRepo.findAllByUser_Id(userId).stream()
                .map(f -> {
                    var r = f.getRecruitment();

                    String thumbnail = imageRepo
                            .findTop1ByRecruitment_IdOrderByIdAsc(r.getId())
                            .map(RecruitmentImage::getImageUrl)
                            .orElse(null);

                    return RecruitmentFavoriteView.builder()
                            .clubId(r.getClub().getId())
                            .recruitmentId(r.getId())
                            .title(r.getTitle())
                            .thumbnailUrl(thumbnail)
                            .build();
                })
                .toList();
    }
}
