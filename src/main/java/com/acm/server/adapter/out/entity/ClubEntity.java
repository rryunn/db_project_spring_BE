package com.acm.server.adapter.out.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "clubs")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "club_id")
    private Long id;

    @Column(name = "club_name", length = 15, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "club_type", nullable = false)
    private ClubType type;

    @Column(name = "club_description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "club_main_activities", columnDefinition = "TEXT")
    private String mainActivities;

    @Column(name = "club_location", length = 100)
    private String location;

    @Column(name = "club_contact_phone_number", length = 15)
    private String contactPhoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "club_category", nullable = false)
    private ClubCategory category;

    @Column(name = "club_sns1", length = 255)
    private String sns1;

    @Column(name = "club_sns2", length = 255)
    private String sns2;

    @Column(name = "club_sns3", length = 255)
    private String sns3;

    @Column(name = "club_sns4", length = 255)
    private String sns4;

    @Column(name = "club_contact_email", length = 50)
    private String contactEmail;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}


