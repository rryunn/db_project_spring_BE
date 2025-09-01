package com.acm.server.adapter.in.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * ✅ 우리 서버 자체 JWT 발급/검증기
 * - AccessToken: sub=userId, roles, exp=짧게 (10~15분)
 * - RefreshToken: sub=userId, sid=sessionId, exp=길게 (2주~1달)
 */
@Component
public class TokenProvider {

    @Value("${jwt.secret}") private String secret;      // HS256 비밀키
    @Value("${jwt.issuer}") private String issuer;      // 발급자 식별자
    @Value("${jwt.access-token-validity-seconds}") private long accessTtl;
    @Value("${jwt.refresh-token-validity-seconds}") private long refreshTtl;

    private Key key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** AccessToken 생성 */
    public String createAccessToken(String userId, Collection<String> roles) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(userId)                    // userId 저장
                .claim("roles", roles)                 // 권한 목록 저장
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(accessTtl)))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    /** RefreshToken 생성 */
    public String createRefreshToken(String userId, String sessionId) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(userId)
                .claim("sid", sessionId)               // 세션 구분용 ID
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(refreshTtl)))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    /** 만료 여부 확인 */
    public boolean isExpired(String token) {
        try {
            return parse(token).getBody().getExpiration().before(new Date());
        } catch (JwtException e) {
            return true; // 파싱 실패도 만료로 간주
        }
    }

    /** 토큰에서 userId(sub) 추출 */
    public String getUserId(String token) {
        return parse(token).getBody().getSubject();
    }

    /** RefreshToken에서 sessionId(sid) 추출 */
    public String getSessionId(String token) {
        Object v = parse(token).getBody().get("sid");
        return v == null ? null : String.valueOf(v);
    }

    /** roles 클레임 추출 */
    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        Object v = parse(token).getBody().get("roles");
        if (v instanceof List<?> list) return list.stream().map(String::valueOf).toList();
        return List.of();
    }

    /** 내부 파싱 공통 메서드 */
    private Jws<Claims> parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
    }
}
