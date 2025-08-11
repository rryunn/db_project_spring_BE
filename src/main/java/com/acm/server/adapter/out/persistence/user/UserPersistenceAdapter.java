package com.acm.server.adapter.out.persistence.user;

import com.acm.server.adapter.out.entity.UserEntity;
import com.acm.server.application.auth.port.out.LoginUserPort;
import com.acm.server.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter implements LoginUserPort {

    private final JpaUserRepository jpaUserRepository;

    @Override
    public User findByUserId(Long userId) {
        UserEntity entity = jpaUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return User.builder()
                   .id(entity.getId())
                   .name(entity.getName())
                   .email(entity.getEmail())
                   .profilePic(entity.getProfilePic())
                   .googleId(entity.getGoogleId())
                   .build();
    }
}
