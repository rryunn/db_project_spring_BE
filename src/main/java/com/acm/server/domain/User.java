package com.acm.server.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class User {
    private Long id;
    private String name;
    private String email;
    private String profilePic;
    private String googleId;
}
