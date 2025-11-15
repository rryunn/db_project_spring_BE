package com.acm.server.adapter.out.persistence.recruitment;
//db 모집공고 테이블이랑 entity를 연결해주는 인터페이스
import com.acm.server.adapter.out.entity.RecruitmentEntity;
import com.acm.server.application.recruitment.dto.RecruitmentCounterProjection;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JpaRecruitmentRepository extends JpaRepository<RecruitmentEntity, Long> {
    //findAll은 기본 제공으로 굳이 작성하지 않아도 된다
    //findById도 이미 제공됨
    //deleteById도 제공해줌.
    //List<RecruitmentEntity> findByEndDateAfterOrderByEndDateAsc(LocalDate now, Pageable pageable);

    @EntityGraph(attributePaths = "club")  // ✅ club 같이 가져오게
    @Query("select r from RecruitmentEntity r")
    List<RecruitmentEntity> findAllWithClub();

    @EntityGraph(attributePaths = "club")
    List<RecruitmentEntity> findByClub_Id(Long clubId);
    @Query("SELECT r FROM RecruitmentEntity r " +
            "WHERE r.type = '상시모집' OR " +
            "(r.type = '수시모집' AND r.endDate >= CURRENT_DATE)")

    @EntityGraph(attributePaths = "club")
    List<RecruitmentEntity> findMainRecruitments();

    @Query("""
        select r.id as id,
               r.viewCount as viewCount,
               r.saveCount as saveCount
        from RecruitmentEntity r
        """)
    List<RecruitmentCounterProjection> findAllForCounterInit();
}
