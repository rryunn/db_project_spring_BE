package com.acm.server.application.recruitment.port.out;

import com.acm.server.domain.Recruitment;

public interface CreateRecruitmentPort {
    Recruitment save(Recruitment recruitment);
}
