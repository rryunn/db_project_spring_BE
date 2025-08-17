package com.acm.server.application.recruitment.port.in;

import com.acm.server.domain.Recruitment;

import java.util.List;

public interface FindRecruitmentUseCase {

    List<Recruitment> findAllRecruitment();
}
