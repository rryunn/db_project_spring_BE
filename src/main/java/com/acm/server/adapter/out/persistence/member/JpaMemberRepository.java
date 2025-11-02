package com.acm.server.adapter.out.persistence.member;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;         
import org.springframework.data.repository.query.Param;      
import com.acm.server.adapter.out.entity.MemberEntity;

public interface JpaMemberRepository extends JpaRepository <MemberEntity, Long> {
    @Query("""
        select distinct m.club.id
        from MemberEntity m
        where m.user.id = :userId
        and m.status = '활동중'
        and m.role in ('회장', '부회장', '임원진')
    """)
    List<Long> findOperatorClubIdsByUserId(@Param("userId") Long userId);

}
