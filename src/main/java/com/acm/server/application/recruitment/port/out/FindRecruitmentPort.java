package com.acm.server.application.recruitment.port.out;

import com.acm.server.domain.Recruitment;

import java.util.List;

public interface FindRecruitmentPort {
    List<Recruitment> findAllRecruitment();
}
