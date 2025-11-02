package com.acm.server.adapter.out.persistence.member;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.acm.server.application.user.port.out.MemberRepositoryPort;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberPersistenceAdapter implements MemberRepositoryPort{
    private final JpaMemberRepository jpa;

    @Override
    public List<Long> findOperatorClubIdsByUserId(Long userId) {
        return jpa.findOperatorClubIdsByUserId(userId);
    }
    
}
