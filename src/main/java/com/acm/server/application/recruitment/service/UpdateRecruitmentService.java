package com.acm.server.application.recruitment.service;

import com.acm.server.application.recruitment.port.in.RecruitmentCommand;
import com.acm.server.application.recruitment.port.in.UpdateRecruitmentCommand;
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
    public Recruitment updateRecruitment(UpdateRecruitmentCommand cmd) {
        // 1) 존재 확인
        var current = updatePort.findRecruitmentByClubId(cmd.clubId())
                .orElseThrow(() -> new IllegalArgumentException("Recruitment not found for clubId=" + cmd.clubId()));

        var now = LocalDateTime.now();
        var toSave = Recruitment.builder()
                .id(current.getId())
                .clubId(current.getClubId())
                .clubName(current.getClubName())
                .createdAt(current.getCreatedAt())
                .updatedAt(now) // 업데이트 시간만 갱신

                // --- PATCH 로직 시작 ---
                .title(
                        cmd.title() != null ? cmd.title() : current.getTitle()
                )
                .description(
                        cmd.description() != null ? cmd.description() : current.getDescription()
                )
                .type(
                        cmd.type() != null ? cmd.type() : current.getType()
                )
                .phoneNumber(
                        cmd.phoneNumber() != null ? cmd.phoneNumber() : current.getPhoneNumber()
                )
                .email(
                        cmd.email() != null ? cmd.email() : current.getEmail()
                )
                .startDate(
                        cmd.startDate() != null ? cmd.startDate() : current.getStartDate()
                )
                .endDate(
                        cmd.endDate() != null ? cmd.endDate() : current.getEndDate()
                )
                .url(
                        cmd.url() != null ? cmd.url() : current.getUrl()
                )
                .build();

        return updatePort.save(toSave);
    }
}
