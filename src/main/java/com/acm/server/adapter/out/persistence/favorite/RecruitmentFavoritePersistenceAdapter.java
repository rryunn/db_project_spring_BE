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
    public boolean saveFavorite(Long userId, Long recruitmentId) {
        RecruitmentFavoriteId id = new RecruitmentFavoriteId(userId, recruitmentId);

        // 이미 있으면 아무 것도 안 하고 false 리턴
        if (favoriteRepo.existsById(id)) {
            return false;
        }

        UserEntity userRef = userRepo.getReferenceById(userId);
        RecruitmentEntity recRef = recruitmentRepo.getReferenceById(recruitmentId);

        var entity = RecruitmentFavoriteEntity.builder()
                .id(id)
                .user(userRef)
                .recruitment(recRef)
                .build();

        favoriteRepo.save(entity);
        return true; // 실제로 하나 추가됨
    }

    @Override
    public boolean deleteFavorite(Long userId, Long recruitmentId) {
        // deleteBy…()가 삭제된 row 수를 반환하게 하기
        long deleted = favoriteRepo.deleteByUser_IdAndId_RecruitmentId(userId, recruitmentId);
        return deleted > 0;
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
