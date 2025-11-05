package com.acm.server.application.recruitment.service;

import com.acm.server.application.club.port.out.FindClubPort;
import com.acm.server.application.recruitment.port.in.CreateRecruitmentUseCase;
import com.acm.server.application.recruitment.port.in.RecruitmentCommand;
import com.acm.server.application.recruitment.port.out.CreateRecruitmentPort;
import com.acm.server.domain.Recruitment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class CreateRecruitmentService implements CreateRecruitmentUseCase {
    private final CreateRecruitmentPort createRecruitmentPort;
    private final FindClubPort findClubPort;

    @Override
    public Recruitment createRecruitment(RecruitmentCommand cmd){

        var club = findClubPort.findClub(cmd.clubId())
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Club not found with id: " + cmd.clubId()));
        var now = LocalDateTime.now();
        var toSave = Recruitment.builder()
                .id(null)
                .clubId(cmd.clubId())
                .clubName(club.getName())
                .title(cmd.title())
                .description(cmd.description())
                .type(cmd.type())
                .phoneNumber(cmd.phoneNumber())
                .email(cmd.email())
                .startDate(cmd.startDate())
                .endDate(cmd.endDate())
                .url(cmd.url())
                .createdAt(now)
                .updatedAt(now)
                .build();
        return createRecruitmentPort.save(toSave);
    }
}
