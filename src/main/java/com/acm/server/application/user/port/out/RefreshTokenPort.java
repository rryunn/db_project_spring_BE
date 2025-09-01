package com.acm.server.application.user.port.out;

import java.time.Duration;

public interface RefreshTokenPort {
    void save(String userId, String sessionId, String refreshToken, Duration ttl);
    boolean exists(String userId, String sessionId, String refreshToken);
    void delete(String userId, String sessionId);
    void deleteAllOfUser(String userId);
}
