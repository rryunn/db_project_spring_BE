package com.acm.server.adapter.in.dto;

import com.acm.server.domain.User;

public record MyInfoResponse(Long id, String name, String email, String profilePic) {
    public static MyInfoResponse from(User u) {
        return new MyInfoResponse(u.getId(), u.getName(), u.getEmail(), u.getProfilePic());
    }
}
