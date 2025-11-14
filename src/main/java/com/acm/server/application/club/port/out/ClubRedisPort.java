package com.acm.server.application.club.port.out;

import java.util.List;
import java.util.Optional;

import com.acm.server.domain.Club;
import com.acm.server.domain.CentralClub;
import com.acm.server.domain.AcademicClub;

public interface ClubRedisPort {

    // 1. 전체 클럽 목록
    Optional<List<Club>> getAllClubs();
    void saveAllClubs(List<Club> clubs);
    void evictAllClubs();

    // 2. 중앙 동아리 상세
    Optional<CentralClub> getCentralClub(Long clubId);
    void saveCentralClub(CentralClub club);
    void evictCentralClub(Long clubId);

    // 3. 학술 동아리 상세
    Optional<AcademicClub> getAcademicClub(Long clubId);
    void saveAcademicClub(AcademicClub club);
    void evictAcademicClub(Long clubId);
}
