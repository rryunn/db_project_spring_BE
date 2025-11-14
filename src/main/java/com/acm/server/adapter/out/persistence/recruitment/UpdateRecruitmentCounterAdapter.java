// src/main/java/com/acm/server/adapter/out/persistence/recruitment/UpdateRecruitmentCounterAdapter.java
package com.acm.server.adapter.out.persistence.recruitment;

import com.acm.server.application.recruitment.port.out.UpdateRecruitmentCounterPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@RequiredArgsConstructor
public class UpdateRecruitmentCounterAdapter implements UpdateRecruitmentCounterPort {

    private final JpaRecruitmentRepository jpaRepository;

    @Override
    @Transactional
    public void updateCounters(Map<Long, Long> viewCounts, Map<Long, Long> saveCounts) {
        if ((viewCounts == null || viewCounts.isEmpty())
                && (saveCounts == null || saveCounts.isEmpty())) {
            return;
        }

        // 업데이트 대상 id 전체 (view/save 키 union)
        Set<Long> ids = new HashSet<>();
        if (viewCounts != null) ids.addAll(viewCounts.keySet());
        if (saveCounts != null) ids.addAll(saveCounts.keySet());
        if (ids.isEmpty()) return;

        var entities = jpaRepository.findAllById(ids);

        entities.forEach(e -> {
            Long id = e.getId();

            if (viewCounts != null && viewCounts.containsKey(id)) {
                e.setViewCount(viewCounts.get(id));
            }
            if (saveCounts != null && saveCounts.containsKey(id)) {
                e.setSaveCount(saveCounts.get(id));
            }
        });

        jpaRepository.saveAll(entities);
    }
}
