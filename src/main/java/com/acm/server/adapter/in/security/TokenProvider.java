package com.acm.server.adapter.in.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TokenProvider {

    @Value("${jwt.secret}") private String secret;
    @Value("${jwt.issuer}") private String issuer;
    @Value("${jwt.access-token-validity-seconds}") private long accessTtl;
    @Value("${jwt.refresh-token-validity-seconds}") private long refreshTtl;

    private Key key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** (기존) AccessToken 생성 — email 없이도 동작 */
    public String createAccessToken(String userId, Collection<String> roles, List<Long> managedClubs) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(userId)
                .claim("roles", roles)
                .claim("managed_clubs", managedClubs)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(accessTtl)))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    /** (신규) AccessToken 생성 — email 포함 */
    public String createAccessToken(String userId, String email, Collection<String> roles, List<Long> managedClubs) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setIssuer(issuer)
                .setSubject(userId)
                .claim("email", email)
                .claim("roles", roles)
                .claim("managed_clubs", managedClubs)
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
                .claim("sid", sessionId)
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
            return true;
        }
    }

    /** userId(sub) */
    public String getUserId(String token) {
        return parse(token).getBody().getSubject();
    }

    /** ✅ email 추출 */
    public String getEmail(String token) {
        Object v = parse(token).getBody().get("email");
        return v == null ? null : String.valueOf(v);
    }

    /** RT의 sessionId */
    public String getSessionId(String token) {
        Object v = parse(token).getBody().get("sid");
        return v == null ? null : String.valueOf(v);
    }

    /** roles */
    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        Object v = parse(token).getBody().get("roles");
        if (v instanceof List<?> list) return list.stream().map(String::valueOf).toList();
        return List.of();
    }

    private Jws<Claims> parse(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
    }

     /** managed_clubs를 List<Long>으로 안전 변환 (List<Integer>/List<String>도 허용) */
    @SuppressWarnings("unchecked")
    public List<Long> getManagedClubs(String token) {
        Object v = parse(token).getBody().get("managed_clubs");
        if (!(v instanceof List<?> list)) return List.of();
        return list.stream()
                .map(obj -> {
                    if (obj == null) return null;
                    String s = String.valueOf(obj);
                    try { return Long.valueOf(s); } catch (NumberFormatException e) { return null; }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
