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
    public Optional<Recruitment> findRecruitmentByClubId(Long clubId) {
        return findRecruitmentPort.findRecruitmentByClubId(clubId);
    }

    @Override
    public void deleteRecruitmentById(Long clubId) {
        findRecruitmentPort.deleteRecruitmentById(clubId);
    }
}

