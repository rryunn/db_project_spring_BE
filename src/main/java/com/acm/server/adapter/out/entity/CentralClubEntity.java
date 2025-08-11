package com.acm.server.adapter.out.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "central_club_details")
public class CentralClubEntity {

    @Id
    private Long clubId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "club_id")
    private ClubEntity club;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Details details;

    public enum Details { 과학기술분과, 레저스포츠분과, 사회활동분과, 연행예술분과, 종교분과, 창작전시분과, 체육분과, 학술언론분과, 준동아리, 기타}
}
