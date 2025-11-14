package com.acm.server.application.recruitment.service;

import com.acm.server.application.recruitment.port.in.FindRecruitmentUseCase;
import com.acm.server.application.recruitment.port.out.FindRecruitmentPort;
import com.acm.server.application.recruitment.port.out.RecruitmentCountRedisPort;
import com.acm.server.application.recruitment.port.out.RecruitmentRedisPort;
import com.acm.server.application.recruitment.port.out.RecruitmentViewHistoryRedisPort;
import com.acm.server.domain.Recruitment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FindRecruitmentService implements FindRecruitmentUseCase {

    private final FindRecruitmentPort findRecruitmentPort;
    private final RecruitmentCountRedisPort counterCachePort;  // Redis 카운터 포트
    private final RecruitmentViewHistoryRedisPort viewHistoryCachePort;
    private final RecruitmentRedisPort listRedisPort;

    // 전체 리스트
    @Override
    public List<Recruitment> findAllRecruitment() {
        // 1) Redis 캐시 먼저 조회
        var cached = listRedisPort.getAllRecruitments();
        List<Recruitment> recruitments = cached
                .orElseGet(() -> {
                    // 2) 캐시에 없으면 DB에서 조회
                    List<Recruitment> fromDb = findRecruitmentPort.findAllRecruitment();
                    // 3) 캐시에 저장
                    listRedisPort.setAllRecruitments(fromDb);
                    return fromDb;
                });

        // 4) view/save 카운터 입혀서 리턴
        return applyCountersIfExists(recruitments);
    }

    // 메인 리스트
    @Override
    public List<Recruitment> getMainRecruitment() {
        // 1) Redis 캐시 먼저 조회
        var cached = listRedisPort.getMainRecruitments();
        List<Recruitment> recruitments = cached
                .orElseGet(() -> {
                    // 2) 캐시에 없으면 DB에서 조회
                    List<Recruitment> fromDb = findRecruitmentPort.getMainRecruitment();
                    // 3) 캐시에 저장
                    listRedisPort.setMainRecruitments(fromDb);
                    return fromDb;
                });

        // 4) view/save 카운터 입혀서 리턴
        return applyCountersIfExists(recruitments);
    }

    // 클럽-모집공고 리스트
    @Override
    public List<Recruitment> findRecruitmentsByClubId(Long clubId) {
        List<Recruitment> recruitments = findRecruitmentPort.findRecruitmentsByClubId(clubId);
        return applyCountersIfExists(recruitments);  
    }

    // // 상세정보, 예전버전
    @Override
    public Optional<Recruitment> findRecruitmentById(Long recruitmentId) {
        return findRecruitmentPort.findRecruitmentById(recruitmentId);
    }
    // 새 버전: userId까지 받아서 조회수 증가 처리
    @Override
    public Optional<Recruitment> findRecruitmentByIdWithView(Long recruitmentId, Long userId) {
        // 1) 공고 조회
        Optional<Recruitment> opt = findRecruitmentPort.findRecruitmentById(recruitmentId);
        if (opt.isEmpty()) return opt;

        // 2) 24시간 내에 이 유저가 본 적 있는지 체크
        boolean shouldIncrease = viewHistoryCachePort.markIfNotViewed(userId, recruitmentId);

        if (shouldIncrease) {
            // 3) 처음 보는 것 → Redis viewCount +1
            counterCachePort.incrementView(recruitmentId, 1L);
        }

        return opt;
    }

    @Override
    public void deleteRecruitmentById(Long recruitmentId) {
        findRecruitmentPort.deleteRecruitmentById(recruitmentId);
    }

    @Override
    public List<String> getRecruitmentImageUrls(Long id) {
        return findRecruitmentPort.getRecruitmentImageUrls(id);
    }

    // ================== 내부 헬퍼 ==================

    private List<Recruitment> applyCountersIfExists(List<Recruitment> recruitments) {
        if (recruitments == null || recruitments.isEmpty()) {
            return recruitments;
        }

        // 1) id 모으기
        var ids = recruitments.stream()
                .map(Recruitment::getId)
                .toList();

        // 2) Redis에서 view/save 카운터 가져오기
        Map<Long, Long> viewMap = counterCachePort.getViewCounts(ids);
        Map<Long, Long> saveMap = counterCachePort.getSaveCounts(ids);

        // 3) Recruitment에 카운터 덮어씌워서 리턴
        return recruitments.stream()
                .map(r -> applyCounters(r, viewMap, saveMap))
                .collect(Collectors.toList());
    }

    private Recruitment applyCounters(
            Recruitment recruitment,
            Map<Long, Long> viewMap,
            Map<Long, Long> saveMap
    ) {
        Long id = recruitment.getId();

        Long viewCount = viewMap.getOrDefault(id, recruitment.getViewCount());
        Long saveCount = saveMap.getOrDefault(id, recruitment.getSaveCount());

        return recruitment.toBuilder()
                .viewCount(viewCount)
                .saveCount(saveCount)
                .build();
    }
}
