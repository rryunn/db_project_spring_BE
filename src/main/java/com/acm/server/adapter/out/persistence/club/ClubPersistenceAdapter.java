package com.acm.server.adapter.out.persistence.club;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.acm.server.adapter.out.entity.AcademicClubEntity;
import com.acm.server.adapter.out.entity.CentralClubEntity;
import com.acm.server.adapter.out.entity.ClubEntity;
import com.acm.server.application.club.dto.UpdateClubReq;
import com.acm.server.application.club.port.out.FindClubPort;
import com.acm.server.application.club.port.out.UpdateClubLogoPort;
import com.acm.server.application.club.port.out.UpdateClubPort;
import com.acm.server.domain.AcademicClub;
import com.acm.server.domain.CentralClub;
import com.acm.server.domain.Club;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ClubPersistenceAdapter implements FindClubPort, UpdateClubPort, UpdateClubLogoPort  {

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

    @Override
    public Club updateClubById(Long clubId, UpdateClubReq req) {
        ClubEntity e = jpaClubRepository.findById(clubId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Club not found"));

        // ✅ 상위 category / name / recruiting / type 등은 절대 변경하지 않음
        if (req.getDescription() != null)     e.setDescription(req.getDescription());
        if (req.getMainActivities() != null)  e.setMainActivities(req.getMainActivities());
        if (req.getLocation() != null)        e.setLocation(req.getLocation());
        if (req.getInstagramUrl() != null)            e.setSns1(req.getInstagramUrl());
        if (req.getYoutubeUrl() != null)            e.setSns2(req.getYoutubeUrl());
        if (req.getLinktreeUrl() != null)            e.setSns3(req.getLinktreeUrl());
        if (req.getClubUrl() != null)            e.setSns4(req.getClubUrl());

        // Dirty Checking으로 @PreUpdate(updatedAt) 동작
        ClubEntity saved = jpaClubRepository.save(e);

        // 수정 결과를 클라이언트에 돌려줄 도메인으로 변환 (상세 매핑)
        return toClubDetail(saved);
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

    private Club toClubDetail(ClubEntity e) {
        return Club.builder()
                .id(e.getId())
                .name(e.getName())
                .description(e.getDescription())
                .location(e.getLocation())
                .MainActivities(e.getMainActivities()) // 도메인 필드명이 단수형이라 주의
                .clubType(e.getType().toString())
                .logoUrl(e.getLogoUrl())
                .category(e.getCategory().toString())
                .instagramUrl(e.getSns1())
                .youtubeUrl(e.getSns2())
                .linktreeUrl(e.getSns3())
                .clubUrl(e.getSns4())
                .isRecruiting(e.isRecruiting())
                .build();
    }


    /** logo_url 컬럼만 부분 업데이트 */
    @Transactional
    @Override
    public void updateClubLogo(Long clubId, String logoUrl) {
        jpaClubRepository.updateLogoUrl(clubId, logoUrl);
    }

}

