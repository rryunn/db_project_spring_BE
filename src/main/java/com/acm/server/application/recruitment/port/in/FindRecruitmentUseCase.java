package com.acm.server.application.recruitment.port.in;

import com.acm.server.domain.Recruitment;

import java.util.List;
import java.util.Optional;

public interface FindRecruitmentUseCase {

    List<Recruitment> findAllRecruitment();
    Optional<Recruitment> findRecruitmentById(Long id);
    void deleteRecruitmentById(Long id);
}
