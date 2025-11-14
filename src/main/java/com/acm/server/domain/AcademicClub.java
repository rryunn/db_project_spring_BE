package com.acm.server.domain;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AcademicClub extends Club {

    // 학술동아리 전용 필드
    private String recruitmentScope;
    private String departmentName;
}
