//DB와 연결
package com.acm.server.application.user.port.out;

import com.acm.server.domain.User;

import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findByGoogleId(String googleId);
    Optional<User> findById(Long id);
    User save(User user); // 도메인 User를 저장/갱신
}
