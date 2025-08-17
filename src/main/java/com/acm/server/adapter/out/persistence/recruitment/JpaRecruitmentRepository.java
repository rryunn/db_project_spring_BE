package com.acm.server.adapter.out.persistence.recruitment;
//db 모집공고 테이블이랑 entity를 연결해주는 인터페이스
import com.acm.server.adapter.out.entity.RecruitmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaRecruitmentRepository extends JpaRepository<RecruitmentEntity, Long> {
    //findAll은 기본 제공으로 굳이 작성하지 않아도 된다
    //findById도 이미 제공됨
    //deleteById도 제공해줌.
}
