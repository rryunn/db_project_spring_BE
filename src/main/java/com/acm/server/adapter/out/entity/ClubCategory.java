package com.acm.server.adapter.out.entity;

import lombok.Getter;

@Getter
public enum ClubCategory {
    SPORTS("스포츠"),
    ACADEMIC("학술"),
    RELIGION("종교"),
    CULTURE_ART("문화/예술"),
    STARTUP("창업"),
    SOCIAL("사교"),
    VOLUNTEER("봉사");

    private final String label;

    ClubCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}