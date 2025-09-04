// adapter/in/security/JwtUserPrincipal.java
package com.acm.server.adapter.in.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public record JwtUserPrincipal(Long userId, String email, Collection<? extends GrantedAuthority> authorities) {}
