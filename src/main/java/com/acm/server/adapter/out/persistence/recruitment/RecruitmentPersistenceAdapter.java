package com.acm.server.adapter.out.persistence.recruitment;

import com.acm.server.adapter.out.entity.RecruitmentEntity;
import com.acm.server.adapter.out.persistence.club.JpaClubRepository;
import com.acm.server.application.recruitment.port.out.CreateRecruitmentPort;
import com.acm.server.application.recruitment.port.out.FindRecruitmentPort;
import com.acm.server.application.recruitment.port.out.UpdateRecruitmentPort;
import com.acm.server.domain.Recruitment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RecruitmentPersistenceAdapter implements FindRecruitmentPort, CreateRecruitmentPort, UpdateRecruitmentPort {

    private final JpaRecruitmentRepository jpaRecruitmentRepository;
    private final JpaClubRepository jpaClubRepository;
    @Override
    public List<Recruitment> findAllRecruitment() {
        return jpaRecruitmentRepository.findAll().stream()
                .map(this::mapToDomain)
                .toList();
    }

    @Override
    public Optional<Recruitment> findRecruitmentByClubId(Long clubId){
        return jpaRecruitmentRepository.findByClubId(clubId)
                .map(this::mapToDomain);
    }

    @Override
    @Transactional
    public void deleteRecruitmentById(Long clubId) {
        jpaRecruitmentRepository.deleteByClub_Id(clubId);
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
}
