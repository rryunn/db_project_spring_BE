package com.acm.server.adapter.out.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "recruitments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruitmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruitment_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "club_id", nullable = false)
    private ClubEntity club;

    @Column(name ="recruitment_title")
    private String title;

    @Column(name ="recruitment_description")
    private String description;

    @Column(name="recruitment_type", nullable = false)
    @Enumerated(EnumType.STRING) // enum이면 추가
    private RecruitmentType type;

    @Column(name="recruitment_phone_number")
    private String phoneNumber;

    @Column(name="recruitment_email")
    private String email;

    @Column(name="recruitment_start_date")
    private LocalDate startDate;

    @Column(name="recruitment_end_date")
    private LocalDate endDate;  

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name="application_url")
    private String url;

    @Column(name="view_count")
    private Long viewCount;

    @Column(name="save_count")
    private Long saveCount;

    @Column(name="semester")
    private String semester;
}


