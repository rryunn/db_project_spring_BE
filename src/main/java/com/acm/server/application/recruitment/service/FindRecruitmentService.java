package com.acm.server.application.recruitment.service;

import com.acm.server.application.recruitment.port.in.FindRecruitmentUseCase;
import com.acm.server.application.recruitment.port.out.FindRecruitmentPort;
import com.acm.server.domain.Recruitment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FindRecruitmentService implements FindRecruitmentUseCase {
    private final FindRecruitmentPort findRecruitmentPort;

    @Override
    public List<Recruitment> findAllRecruitment() {
        return findRecruitmentPort.findAllRecruitment();
    }
}

