package com.acm.server.adapter.in.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("clubGuard")
public class ClubGuard {

    /**
     * 토큰에 포함된 managed_clubs 배열 안에 clubId가 있는지 확인
     */
    public boolean hasManagedClub(Long clubId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return false;
        }

        // claim에서 managed_clubs 꺼내기
        List<?> rawList = jwt.getClaim("managed_clubs");
        System.out.println(rawList);
        if (rawList == null) return false;

        // Long 변환 후 포함 여부 검사
        return rawList.stream()
                .map(Object::toString)
                .map(Long::valueOf)
                .anyMatch(id -> id.equals(clubId));
    }
}
