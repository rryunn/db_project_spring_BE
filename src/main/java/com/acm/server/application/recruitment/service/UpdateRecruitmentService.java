package com.acm.server.application.recruitment.service;

import com.acm.server.application.recruitment.port.in.RecruitmentCommand;
import com.acm.server.application.recruitment.port.in.UpdateRecruitmentUseCase;
import com.acm.server.application.recruitment.port.out.UpdateRecruitmentPort;
import com.acm.server.domain.Recruitment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UpdateRecruitmentService implements UpdateRecruitmentUseCase {
    private final UpdateRecruitmentPort updatePort;

    @Override
    @Transactional
    public Recruitment updateRecruitment(RecruitmentCommand cmd) {
        // 1) 존재 확인
        var current = updatePort.findRecruitmentByClubId(cmd.clubId())
                .orElseThrow(() -> new IllegalArgumentException("Recruitment not found for clubId=" + cmd.clubId()));

        // 2) 기존 createdAt은 유지, 나머지 필드 갱신
        var now = LocalDateTime.now();
        var toSave = Recruitment.builder()
                .id(current.getId())                 // ← 기존 ID 유지
                .clubId(current.getClubId())         // ← clubId 고정
                .clubName(current.getClubName())     // (도메인에 있으면 유지/또는 null)
                .title(cmd.title())
                .description(cmd.description())
                .type(cmd.type())
                .phoneNumber(cmd.phoneNumber())
                .email(cmd.email())
                .startDate(cmd.startDate())
                .endDate(cmd.endDate())
                .url(cmd.url())
                .createdAt(current.getCreatedAt())   // ← 유지
                .updatedAt(now)                      // ← 갱신
                .build();

        return updatePort.save(toSave);
    }
}
