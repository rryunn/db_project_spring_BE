package com.acm.server.adapter.out.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "academic_club_details")
public class AcademicClubEntity {

    @Id
    private Long clubId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "club_id")
    private ClubEntity club;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecruitmentScope recruitmentScope;

    @Column(length = 15, nullable = false)
    private String departmentName;

    public enum RecruitmentScope { 전공무관, 해당단과대, 해당학과 }
}