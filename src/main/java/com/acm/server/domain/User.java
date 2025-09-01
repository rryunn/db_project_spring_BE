package com.acm.server.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    private String email;
    private String profilePic;
    private String googleId;

    /** 최초 구글 로그인 시 가입용 팩토리 메서드 */
    public static User createFromGoogle(String googleId, String email, String name, String profilePic) {
        return User.builder()
                .googleId(googleId)
                .email(email)
                .name(name)
                .profilePic(profilePic)
                .build();
    }

    /** 로그인할 때마다 프로필 정보 최신화 */
    public User updateFromGoogle(String email, String name, String profilePic) {
        this.email = email;
        this.name = name;
        this.profilePic = profilePic;
        return this;
    }

    /** 권한 조회 (추후 Role Enum 도입 가능) */
    public List<String> rolesAsStrings() {
        return List.of("ROLE_USER");
    }
}

