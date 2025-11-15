package com.acm.server.application.recruitment.service;

import com.acm.server.application.recruitment.port.in.FindRecruitmentUseCase;
import com.acm.server.application.recruitment.port.out.FindRecruitmentPort;
import com.acm.server.application.recruitment.port.out.RecruitmentCountRedisPort;
import com.acm.server.application.recruitment.port.out.RecruitmentImageRedisPort;
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
    private final RecruitmentImageRedisPort recruitmentImageRedisPort;

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

    // 상세정보, 예전버전
    @Override
    public Optional<Recruitment> findRecruitmentById(Long recruitmentId) {
        return findRecruitmentPort.findRecruitmentById(recruitmentId);
    }
    
    //상세정보, 새버전
    @Override
    public Optional<Recruitment> findRecruitmentByIdWithView(Long recruitmentId, Long userId) {

        // 1) 상세 조회 Redis 캐시 조회
        Optional<Recruitment> cached = listRedisPort.getRecruitment(recruitmentId);

        Recruitment recruitment = cached.orElseGet(() -> {
            // 2) 캐시에 없으면 DB 조회
            Recruitment fromDb = findRecruitmentPort.findRecruitmentById(recruitmentId)
                    .orElse(null);
            if (fromDb == null) return null;

            // 3) DB 데이터를 Redis에 캐시
            listRedisPort.setRecruitment(recruitmentId, fromDb);
            return fromDb;
        });

        if (recruitment == null) return Optional.empty();

        // 4) view history 체크 (24시간 이내 중복 방지)
        boolean shouldIncrease = viewHistoryCachePort.markIfNotViewed(userId, recruitmentId);

        if (shouldIncrease) {
            // 5) Redis 카운터 증가
            counterCachePort.incrementView(recruitmentId, 1L);

            long baseView = safeLong(recruitment.getViewCount());

            // 상세 캐시에도 즉시 반영 (optional)
            Recruitment updated = recruitment.toBuilder()
                    .viewCount(baseView + 1)
                    .build();

            listRedisPort.setRecruitment(recruitmentId, updated);
            recruitment = updated;
        }

        // 6) 최신 view/save 카운터 입혀서 리턴
        Map<Long, Long> viewMap = counterCachePort.getViewCounts(List.of(recruitmentId));
        Map<Long, Long> saveMap = counterCachePort.getSaveCounts(List.of(recruitmentId));

        long finalView = viewMap.getOrDefault(recruitmentId, safeLong(recruitment.getViewCount()));
        long finalSave = saveMap.getOrDefault(recruitmentId, safeLong(recruitment.getSaveCount()));

        Recruitment finalR = recruitment.toBuilder()
                .viewCount(finalView)
                .saveCount(finalSave)
                .build();

        return Optional.of(finalR);
    }

    @Override
    public void deleteRecruitmentById(Long recruitmentId) {
        findRecruitmentPort.deleteRecruitmentById(recruitmentId);
        listRedisPort.evictRecruitment(recruitmentId);
    }

    @Override
    public List<String> getRecruitmentImageUrls(Long id) {
        // 1) Redis 캐시 먼저 조회
        return recruitmentImageRedisPort.getImageUrls(id)
                .orElseGet(() -> {
                    // 2) 캐시에 없으면 DB 조회
                    List<String> fromDb = findRecruitmentPort.getRecruitmentImageUrls(id);
                    // 3) 캐시에 저장
                    recruitmentImageRedisPort.setImageUrls(id, fromDb);
                    return fromDb;
                });
    }

    // ================== 내부 헬퍼 ==================

    private long safeLong(Long value) {
    return value != null ? value : 0L;
    }

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

        long baseView = safeLong(recruitment.getViewCount());
        long baseSave = safeLong(recruitment.getSaveCount());

        long viewCount = viewMap.getOrDefault(id, baseView);
        long saveCount = saveMap.getOrDefault(id, baseSave);

        return recruitment.toBuilder()
                .viewCount(viewCount)
                .saveCount(saveCount)
                .build();
    }
}
