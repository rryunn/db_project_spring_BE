package com.acm.server.application.recruitment.service;

import com.acm.server.application.recruitment.dto.RecruitmentCounterProjection;
import com.acm.server.application.recruitment.port.out.FindRecruitmentPort;
import com.acm.server.application.recruitment.port.out.RecruitmentCountRedisPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecruitmentViewCountWarmupService implements ApplicationRunner {

    private final FindRecruitmentPort findRecruitmentPort;
    private final RecruitmentCountRedisPort counterCachePort;

    @Override
    public void run(ApplicationArguments args) {

        log.info("[Warmup] Loading recruitment view/save counts into Redis…");

        var projections = findRecruitmentPort.findAllForCounterInit();

        Map<Long, Long> viewCountMap = projections.stream()
                .collect(Collectors.toMap(
                        RecruitmentCounterProjection::getId,
                        p -> p.getViewCount() != null ? p.getViewCount() : 0L
                ));

        Map<Long, Long> saveCountMap = projections.stream()
                .collect(Collectors.toMap(
                        RecruitmentCounterProjection::getId,
                        p -> p.getSaveCount() != null ? p.getSaveCount() : 0L
                ));

        counterCachePort.putAllViewCounts(viewCountMap);
        counterCachePort.putAllSaveCounts(saveCountMap);

        log.info("[Warmup] Loaded {} recruitment counters (view/save)", projections.size());
    }
}

