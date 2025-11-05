package com.acm.server.application.recruitment.dto;

import com.acm.server.adapter.out.entity.RecruitmentType;
import com.acm.server.application.recruitment.port.in.UpdateRecruitmentCommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRecruitmentReq {
    private String title;
    private String description;
    private RecruitmentType type;

    private String phoneNumber;
    private String email;

    private LocalDate startDate;
    private LocalDate endDate;

    private String url;
    // DTO를 Command 객체로 변환
    public UpdateRecruitmentCommand toCommand(Long clubId) {
        return new UpdateRecruitmentCommand(
                clubId,
                this.title,
                this.description,
                this.type,
                this.phoneNumber,
                this.email,
                this.startDate,
                this.endDate,
                this.url // 2. url 필드 전달
        );
    }
}

