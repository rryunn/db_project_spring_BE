//package com.acm.server.adapter.out.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(
//    name = "club_favorites",
//    uniqueConstraints = {
//        @UniqueConstraint(columnNames = {"user_id", "club_id"})
//    }
//)
//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class ClubFavoriteEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "favorite_id")
//    private Long id; // 단일 PK
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private UserEntity user;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "club_id", nullable = false)
//    private ClubEntity club;
//
//    @Column(name = "created_at", nullable = false, updatable = false)
//    private LocalDateTime createdAt;
//
//    // 즐겨찾기 생성 시 createdAt 자동 설정
//    @PrePersist
//    public void onCreate() {
//        this.createdAt = LocalDateTime.now();
//    }
//}
