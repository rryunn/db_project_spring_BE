package com.acm.server.application.recruitment.port.out;

import java.util.List;

import com.acm.server.domain.Recruitment;

public interface CreateRecruitmentPort {
    Recruitment save(Recruitment recruitment);

    List<Recruitment> findAllRecruitmentForRedis();
}
