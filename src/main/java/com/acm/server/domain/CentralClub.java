package com.acm.server.domain;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CentralClub extends Club {

    // 중앙동아리 전용 필드
    private String details;
}
