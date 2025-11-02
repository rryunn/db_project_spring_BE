// adapter/in/security/JwtAuthenticationFilter.java
package com.acm.server.adapter.in.security;

import com.acm.server.adapter.in.security.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    public JwtAuthenticationFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                if (!tokenProvider.isExpired(token)) {
                    Long userId = Long.valueOf(tokenProvider.getUserId(token));

                    var authorities = tokenProvider.getRoles(token).stream()
                            .map(SimpleGrantedAuthority::new)
                            .toList();

                    var managedClubs = tokenProvider.getManagedClubs(token); // ★ 추가

                    var principal = new JwtUserPrincipal(
                            userId,
                            tokenProvider.getEmail(token),
                            authorities,
                            managedClubs
                    );

                    var authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception ignore) {
                // 유효하지 않으면 인증 미설정으로 진행
            }
        }
        chain.doFilter(request, response);
    }
}
