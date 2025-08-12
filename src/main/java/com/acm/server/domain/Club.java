package com.acm.server.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Club {
    private Long id;
    private String name;
    private String description;
}
