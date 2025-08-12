package com.acm.server.adapter.out.persistence.club;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.acm.server.adapter.in.response.Response;
import com.acm.server.adapter.out.entity.ClubEntity;
import com.acm.server.application.club.port.out.FindClubPort;
import com.acm.server.domain.Club;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ClubPersistenceAdapter implements FindClubPort{
    private final JpaClubRepository jpaClubRepository;

    public List<Club> findAllClub(){
        List<ClubEntity> clubEntities = jpaClubRepository.findAll();

        return clubEntities.stream()
        .map(entity -> Club.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .build()
        )
        .toList();

    }
}
