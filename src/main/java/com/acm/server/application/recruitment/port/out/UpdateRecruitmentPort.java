package com.acm.server.application.recruitment.port.out;

import com.acm.server.application.recruitment.port.in.RecruitmentCommand;
import com.acm.server.domain.Recruitment;

import java.util.Optional;

public interface UpdateRecruitmentPort {

    // clubId로 현재 모집공고 로딩(존재 검증용)
    Optional<Recruitment> findRecruitmentByClubId(Long clubId);

    // 저장(업데이트)
    Recruitment save(Recruitment recruitment);
}
