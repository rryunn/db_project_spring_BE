package com.acm.server.adapter.out.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "recruitment_favorites")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruitmentFavoriteEntity {

    @EmbeddedId
    private RecruitmentFavoriteId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")  // 복합키 userId 매핑
    @JoinColumn(name = "user_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_favorite_user"))
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("recruitmentId")  // 복합키 recruitmentId 매핑
    @JoinColumn(name = "recruitment_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_favorite_recruitment"))
    private RecruitmentEntity recruitment;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
