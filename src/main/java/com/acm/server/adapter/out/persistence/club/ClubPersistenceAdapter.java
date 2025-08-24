package com.acm.server.adapter.out.persistence.club;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.acm.server.adapter.in.response.Response;
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
public class ClubPersistenceAdapter implements FindClubPort{
    private final JpaClubRepository jpaClubRepository;
    private final JpaAcademicClubRepository jpaAcademicClubRepository;
    private final JpaCentralClubRepository jpaCentralClubRepository;

    public List<Club> findAllClub(){
        List<ClubEntity> clubEntities = jpaClubRepository.findAll();

        return clubEntities.stream()
        .map(entity -> Club.builder()
                .id(entity.getId())
                .name(entity.getName())
                .clubType(entity.getType().toString())
                .logoUrl(entity.getLogoUrl())
                .isRecruiting(entity.isRecruiting())
                .category(entity.getCategory().toString())
                .build()
        )
        .toList();
    }

    public CentralClub findCentralClub(Long clubId){
        Object row = jpaCentralClubRepository.findCentralClubById(clubId)
            .orElseThrow(() -> new RuntimeException("해당 클럽 없음"));

        Object[] arr = (Object[]) row;
        ClubEntity c = (ClubEntity) arr[0];
        CentralClubEntity d = (CentralClubEntity) arr[1];

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
                .details(d.getDetails().name())  // Enum → String
                .build();
    }

    public AcademicClub findAcademicClub(Long clubId){
        Object row = jpaAcademicClubRepository.findAcademicClubById(clubId)
            .orElseThrow(() -> new RuntimeException("해당 클럽 없음"));

        Object[] arr = (Object[]) row;
        ClubEntity c = (ClubEntity) arr[0];
        AcademicClubEntity d = (AcademicClubEntity) arr[1];

        // ✅ Builder 패턴 사용
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
    public List<Club> findFilterClub(String type, String category,Boolean isRecruiting, String department){
        List<ClubEntity> clubEntities = jpaClubRepository.findFilterClub(type, category, isRecruiting, department);

        return clubEntities.stream()
        .map(entity -> Club.builder()
                .id(entity.getId())
                .name(entity.getName())
                .clubType(entity.getType().toString())
                .logoUrl(entity.getLogoUrl())
                .isRecruiting(entity.isRecruiting())
                .category(entity.getCategory().toString())
                .build()
        )
        .toList();
    }
}
