package com.acm.server.application.recruitment.port.out;

import com.acm.server.application.recruitment.port.in.RecruitmentCommand;
import com.acm.server.domain.Recruitment;

import java.util.Optional;

public interface UpdateRecruitmentPort {

    Optional<Recruitment> findRecruitmentById(Long recruitmentId);

    // 저장(업데이트)
    Recruitment save(Recruitment recruitment);
}
