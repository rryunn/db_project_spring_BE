package com.acm.server.application.recruitment.port.in;

import com.acm.server.domain.Recruitment;

public interface UpdateRecruitmentUseCase {
    Recruitment updateRecruitment(UpdateRecruitmentCommand cmd);

}
