package com.acm.server.adapter.out.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RecruitmentFavoriteId implements Serializable {

    @Column(name = "user_id", columnDefinition = "BIGINT UNSIGNED")
    private Long userId;

    @Column(name = "recruitment_id", columnDefinition = "BIGINT UNSIGNED")
    private Long recruitmentId;
}
