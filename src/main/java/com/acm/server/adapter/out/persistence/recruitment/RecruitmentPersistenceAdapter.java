package com.acm.server.adapter.out.persistence.recruitment;

import com.acm.server.adapter.out.entity.RecruitmentEntity;
import com.acm.server.application.recruitment.port.out.FindRecruitmentPort;
import com.acm.server.domain.Recruitment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RecruitmentPersistenceAdapter implements FindRecruitmentPort {

    private final JpaRecruitmentRepository jpaRecruitmentRepository;

    @Override
    public List<Recruitment> findAllRecruitment() {
        return jpaRecruitmentRepository.findAll().stream()
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
}
