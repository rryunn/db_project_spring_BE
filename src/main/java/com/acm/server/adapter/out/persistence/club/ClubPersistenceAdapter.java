package com.acm.server.adapter.out.persistence.club;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.acm.server.adapter.out.entity.AcademicClubEntity;
import com.acm.server.adapter.out.entity.CentralClubEntity;
import com.acm.server.adapter.out.entity.ClubEntity;
import com.acm.server.application.club.port.out.FindClubPort;
import com.acm.server.domain.AcademicClub;
import com.acm.server.domain.CentralClub;
import com.acm.server.domain.Club;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ClubPersistenceAdapter implements FindClubPort {

    private final JpaClubRepository jpaClubRepository;
    private final JpaAcademicClubRepository jpaAcademicClubRepository;
    private final JpaCentralClubRepository jpaCentralClubRepository;

    @Override
    public List<Club> findAllClub() {
        return jpaClubRepository.findAll()
                .stream()
                .map(this::toClub)
                .toList();
    }

    @Override
    public Optional<CentralClub> findCentralClub(Long clubId) {
        // findCentralClubById가 Optional<Object> (Object[] join row) 라는 전제
        return jpaCentralClubRepository.findCentralClubById(clubId)
                .map(row -> {
                    Object[] arr = (Object[]) row;         // (ClubEntity, CentralClubEntity)
                    ClubEntity c = (ClubEntity) arr[0];
                    CentralClubEntity d = (CentralClubEntity) arr[1];
                    return toCentralClub(c, d);
                });
    }

    @Override
    public Optional<AcademicClub> findAcademicClub(Long clubId) {
        return jpaAcademicClubRepository.findAcademicClubById(clubId)
                .map(row -> {
                    Object[] arr = (Object[]) row;         // (ClubEntity, AcademicClubEntity)
                    ClubEntity c = (ClubEntity) arr[0];
                    AcademicClubEntity d = (AcademicClubEntity) arr[1];
                    return toAcademicClub(c, d);
                });
    }

    @Override
    public Optional<Club> findClub(Long clubId) {
        return jpaClubRepository.findById(clubId)
                .map(this::toClub);
    }

    @Override
    public List<Club> findFilterClub(String type, String category, Boolean isRecruiting, String department) {
        return jpaClubRepository.findFilterClub(type, category, isRecruiting, department)
                .stream()
                .map(this::toClub)
                .toList();
    }

    // ========= Mappers =========
    private Club toClub(ClubEntity e) {
        return Club.builder()
                .id(e.getId())
                .name(e.getName())
                .clubType(e.getType().toString())
                .logoUrl(e.getLogoUrl())
                .isRecruiting(e.isRecruiting())
                .category(e.getCategory().toString())
                .build();
    }

    private CentralClub toCentralClub(ClubEntity c, CentralClubEntity d) {
        return CentralClub.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .mainActivities(c.getMainActivities())
                .location(c.getLocation())
                .contactPhoneNumber(c.getContactPhoneNumber())
                .instagramUrl(c.getSns1())
                .youtubeUrl(c.getSns2())
                .linktreeUrl(c.getSns3())
                .clubUrl(c.getSns4())
                .contactEmail(c.getContactEmail())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .clubType(c.getType().toString())
                .logoUrl(c.getLogoUrl())
                .isRecruiting(c.isRecruiting())
                .category(c.getCategory().toString())
                .details(d.getDetails().name())
                .build();
    }

    private AcademicClub toAcademicClub(ClubEntity c, AcademicClubEntity d) {
        return AcademicClub.builder()
                .id(c.getId())
                .name(c.getName())
                .description(c.getDescription())
                .mainActivities(c.getMainActivities())
                .location(c.getLocation())
                .contactPhoneNumber(c.getContactPhoneNumber())
                .instagramUrl(c.getSns1())
                .youtubeUrl(c.getSns2())
                .linktreeUrl(c.getSns3())
                .clubUrl(c.getSns4())
                .contactEmail(c.getContactEmail())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .clubType(c.getType().toString())
                .logoUrl(c.getLogoUrl())
                .isRecruiting(c.isRecruiting())
                .category(c.getCategory().toString())
                .recruitmentScope(d.getRecruitmentScope().toString())
                .departmentName(d.getDepartmentName())
                .build();
    }
}

