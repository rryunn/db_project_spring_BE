package com.acm.server.adapter.in.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 매 요청마다 실행되는 JWT 인증 필터.
 * 1) Authorization 헤더에서 Bearer <AT> 추출
 * 2) AT 유효/만료 검증
 * 3) sub(userId)로 UserDetails 조회 → SecurityContext에 인증 설정
 * 실패 시 인증 미설정 상태로 다음 필터로 진행(최종 401은 EntryPoint가 처리)
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final UserDetailsService userDetailsService; // adapter.out.persistence 에서 구현/주입

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        String header = req.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                if (!tokenProvider.isExpired(token)) {
                    String userId = tokenProvider.getUserId(token);
                    UserDetails user = userDetailsService.loadUserByUsername(userId);

                    var auth = new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception ignore) {
                // 파싱 실패/서명 오류/만료/블랙리스트 등 → 인증 미설정
            }
        }

        chain.doFilter(req, res);
    }
}
