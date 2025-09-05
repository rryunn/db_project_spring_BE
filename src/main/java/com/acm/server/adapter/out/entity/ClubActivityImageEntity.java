package com.acm.server.adapter.out.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "club_activity_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubActivityImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "club_activity_image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private ClubEntity club;  // ClubEntity와 매핑

    @Column(name = "club_activity_image_url", nullable = false, length = 255)
    private String imageUrl;
}
