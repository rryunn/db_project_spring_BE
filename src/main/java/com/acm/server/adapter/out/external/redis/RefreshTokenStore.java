package com.acm.server.adapter.out.external.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.acm.server.application.user.port.out.RefreshTokenPort;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Base64;
import java.util.Set;

/**
 * ✅ RefreshToken 저장/검증/삭제 관리
 * - Redis Key: rt:{userId}:{sessionId}
 * - Value: RefreshToken 해시(SHA-256)
 * - TTL: RT 만료 시간과 동일
 *
 * 기능:
 * - save(): RT 저장
 * - exists(): 주어진 RT가 유효한지(재사용 감지)
 * - delete(): 특정 세션 RT 삭제
 * - deleteAllOfUser(): 특정 유저의 모든 세션 RT 삭제
 */
@Component
@RequiredArgsConstructor
public class RefreshTokenStore implements RefreshTokenPort{

    private final StringRedisTemplate redis;

    private String key(String userId, String sessionId) {
        return "rt:%s:%s".formatted(userId, sessionId);
    }

    /** RT를 SHA-256 해시화 */
    private String sha256(String token) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(md.digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    /** RT 저장 (TTL 설정) */
    public void save(String userId, String sessionId, String refreshToken, Duration ttl) {
        redis.opsForValue().set(key(userId, sessionId), sha256(refreshToken), ttl);
    }

    /** RT 존재 여부 확인 (재사용 감지용) */
    public boolean exists(String userId, String sessionId, String refreshToken) {
        String stored = redis.opsForValue().get(key(userId, sessionId));
        return stored != null && stored.equals(sha256(refreshToken));
    }

    /** 특정 세션 RT 삭제 */
    public void delete(String userId, String sessionId) {
        redis.delete(key(userId, sessionId));
    }

    /** 특정 유저의 모든 세션 RT 삭제 (재사용 감지 시 전체 강제 로그아웃) */
    public void deleteAllOfUser(String userId) {
        Set<String> keys = redis.keys("rt:" + userId + ":*");
        if (keys != null && !keys.isEmpty()) redis.delete(keys);
    }
}
