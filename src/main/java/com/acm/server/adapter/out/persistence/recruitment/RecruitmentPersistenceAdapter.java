package com.acm.server.adapter.out.persistence.recruitment;

import com.acm.server.adapter.out.entity.RecruitmentEntity;
import com.acm.server.domain.RecruitmentImage;
import com.acm.server.adapter.out.persistence.club.JpaClubRepository;
import com.acm.server.application.recruitment.dto.RecruitmentCounterProjection;
import com.acm.server.application.recruitment.port.out.CreateRecruitmentPort;
import com.acm.server.application.recruitment.port.out.FindRecruitmentPort;
import com.acm.server.application.recruitment.port.out.ManageRecruitmentImagesPort;
import com.acm.server.application.recruitment.port.out.UpdateRecruitmentPort;
import com.acm.server.domain.Recruitment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RecruitmentPersistenceAdapter implements FindRecruitmentPort, CreateRecruitmentPort, UpdateRecruitmentPort, ManageRecruitmentImagesPort {

    private final JpaRecruitmentRepository jpaRecruitmentRepository;
    private final JpaClubRepository jpaClubRepository;
    private final JpaRecruitmentImageRepository recruitmentImageRepository;


    @Override
    public List<Recruitment> findAllRecruitment() {
        return jpaRecruitmentRepository.findAll().stream()
                .map(this::mapToDomain)
                .toList();
    }


    @Override
    public List<Recruitment> getMainRecruitment() {
        return jpaRecruitmentRepository.findMainRecruitments()
                .stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Transactional
    public void deleteRecruitmentById(Long recruitmentId) {
        jpaRecruitmentRepository.deleteById(recruitmentId);
    }

    @Override
    @Transactional
    public Recruitment save(Recruitment d) {
        var club = jpaClubRepository.findById(d.getClubId())
                .orElseThrow(() -> new IllegalArgumentException("club not found: " + d.getClubId()));

        var entity = RecruitmentEntity.builder()
                .id(d.getId()) // 생성이면 null
                .club(club)
                .title(d.getTitle())
                .description(d.getDescription())
                .type(d.getType())
                .phoneNumber(d.getPhoneNumber())
                .email(d.getEmail())
                .startDate(d.getStartDate())
                .endDate(d.getEndDate())
                .url(d.getUrl())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();

        var saved = jpaRecruitmentRepository.save(entity);
        return mapToDomain(saved);
    }

    @Override
    public List<Recruitment> findRecruitmentsByClubId(Long clubId){
        return jpaRecruitmentRepository.findByClub_Id(clubId)
                .stream()
                .map(this::mapToDomain)
                .toList();
    }
    private Recruitment mapToDomain(RecruitmentEntity entity) {
        return Recruitment.builder()
                .id(entity.getId())
                .clubId(entity.getClub().getId())      // ClubEntity에서 가져옴
                .clubName(entity.getClub().getName())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .type(entity.getType())
                .phoneNumber(entity.getPhoneNumber())
                .email(entity.getEmail())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .url(entity.getUrl())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    public List<String> getRecruitmentImageUrls(Long id) {
       return recruitmentImageRepository.findByRecruitment_Id(id).stream()
                .map(com.acm.server.adapter.out.entity.RecruitmentImage::getImageUrl)
                .toList();
    }

    @Override
    public Optional<Recruitment> findRecruitmentById(Long recruitmentId) {
        return jpaRecruitmentRepository.findById(recruitmentId)
        .map(this::mapToDomain);
    }

    @Override
    public Optional<RecruitmentImage> findByRecruitmentIdAndUrl(Long recruitmentId, String imageUrl) {
        // 4. JpaRepo의 새 메서드 호출
        return recruitmentImageRepository.findByRecruitment_IdAndImageUrl(recruitmentId, imageUrl)
                .map(this::mapImageToDomain); // 엔티티 -> 도메인
    }

    @Override
    @Transactional
    public RecruitmentImage save(RecruitmentImage imageDomain) {
        // 5. 부모(RecruitmentEntity)를 찾음
        RecruitmentEntity recruitment = jpaRecruitmentRepository.findById(imageDomain.getRecruitmentId())
                .orElseThrow(() -> new IllegalArgumentException("Recruitment not found: " + imageDomain.getRecruitmentId()));

        // 6. 도메인 -> 엔티티 변환
        var entity = com.acm.server.adapter.out.entity.RecruitmentImage.builder()
                .id(imageDomain.getId()) // 신규면 null
                .recruitment(recruitment) // 부모 엔티티 연결
                .imageUrl(imageDomain.getImageUrl())
                .build();

        // 7. DB 저장
        var savedEntity = recruitmentImageRepository.save(entity);

        // 8. 엔티티 -> 도메인 변환 후 반환
        return mapImageToDomain(savedEntity);
    }

    @Override
    @Transactional
    public void deleteByRecruitmentIdAndUrl(Long recruitmentId, String imageUrl) {
        // 9. JpaRepo의 새 메서드 호출
        recruitmentImageRepository.deleteByRecruitment_IdAndImageUrl(recruitmentId, imageUrl);
    }

    @Override
    public List<RecruitmentImage> findAllByRecruitmentId(Long recruitmentId) {
        // 10. JpaRepo의 기존 메서드 호출
        return recruitmentImageRepository.findByRecruitment_Id(recruitmentId).stream()
                .map(this::mapImageToDomain) // 엔티티 -> 도메인
                .toList();
    }

    @Override
    @Transactional
    public void deleteAllByRecruitmentId(Long recruitmentId) {
        // 11. JpaRepo의 새 메서드 호출
        recruitmentImageRepository.deleteByRecruitment_Id(recruitmentId);
    }

    //캐싱용도
    @Override
    public List<RecruitmentCounterProjection> findAllForCounterInit() {
        return jpaRecruitmentRepository.findAllForCounterInit();
    }

    /**
     * (신규) RecruitmentImage 엔티티 -> 도메인 매퍼
     */
    private RecruitmentImage mapImageToDomain(com.acm.server.adapter.out.entity.RecruitmentImage entity) {
        return RecruitmentImage.builder()
                .id(entity.getId())
                .recruitmentId(entity.getRecruitment().getId())
                .imageUrl(entity.getImageUrl())
                .build();
    }

}
