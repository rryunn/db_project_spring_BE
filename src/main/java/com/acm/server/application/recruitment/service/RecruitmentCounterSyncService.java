// src/main/java/com/acm/server/application/recruitment/service/RecruitmentCounterSyncService.java
package com.acm.server.application.recruitment.service;

import com.acm.server.application.recruitment.port.out.FindRecruitmentPort;
import com.acm.server.application.recruitment.port.out.RecruitmentCountRedisPort;
import com.acm.server.application.recruitment.port.out.UpdateRecruitmentCounterPort;
import com.acm.server.domain.Recruitment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecruitmentCounterSyncService {

    private final FindRecruitmentPort findRecruitmentPort;
    private final RecruitmentCountRedisPort counterCachePort;
    private final UpdateRecruitmentCounterPort updateRecruitmentCounterPort;

    /**
     * Redis 카운터를 주기적으로 DB와 동기화.
     * cron: 매 5분마다 (원하면 조정 가능)
     */
    @Scheduled(cron = "0 */5 * * * *") // 5분마다
    public void syncCounters() {
        log.info("[RecruitmentCounterSync] Start syncing counters Redis -> DB");

        // 1) 모든 모집공고 id 목록 가져오기
        List<Recruitment> recruitments = findRecruitmentPort.findAllRecruitment();
        if (recruitments.isEmpty()) {
            log.info("[RecruitmentCounterSync] No recruitments found. Skip.");
            return;
        }

        var ids = recruitments.stream()
                .map(Recruitment::getId)
                .toList();

        // 2) Redis 에서 view/save 카운터 조회
        Map<Long, Long> viewMap = counterCachePort.getViewCounts(ids);
        Map<Long, Long> saveMap = counterCachePort.getSaveCounts(ids);

        // (없을 수도 있으니 null/empty 체크는 adapter 내부에서 해도 OK)
        updateRecruitmentCounterPort.updateCounters(viewMap, saveMap);

        log.info("[RecruitmentCounterSync] Synced {} recruitment counters", ids.size());
    }
}
