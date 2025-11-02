package com.acm.server.adapter.in.security;

import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;
import java.util.List;

public record JwtUserPrincipal(
        Long userId,
        String email,
        Collection<? extends GrantedAuthority> authorities,
        List<Long> managedClubs
) {}
