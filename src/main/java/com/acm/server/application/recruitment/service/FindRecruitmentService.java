package com.acm.server.application.recruitment.service;

import com.acm.server.application.recruitment.port.in.FindRecruitmentUseCase;
import com.acm.server.application.recruitment.port.out.FindRecruitmentPort;
import com.acm.server.domain.Recruitment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FindRecruitmentService implements FindRecruitmentUseCase {
    private final FindRecruitmentPort findRecruitmentPort;

    @Override
    public List<Recruitment> findAllRecruitment() {
        return findRecruitmentPort.findAllRecruitment();
    }

    @Override
    public Optional<Recruitment> findRecruitmentById(Long id) {
        return findRecruitmentPort.findRecruitmentById(id);
    }

    @Override
    public void deleteRecruitmentById(Long id) {
        findRecruitmentPort.deleteRecruitmentById(id);
    }
}

