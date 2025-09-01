package com.acm.server.adapter.out.persistence.user;

import com.acm.server.adapter.out.entity.UserEntity;
import com.acm.server.application.auth.port.out.LoginUserPort;
import com.acm.server.application.user.port.out.UserRepositoryPort;
import com.acm.server.domain.User;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final JpaUserRepository jpa; // Spring Data JPA (UserEntity)

    private User toDomain(UserEntity e) {
        return User.builder()
            .id(e.getId()).googleId(e.getGoogleId())
            .email(e.getEmail()).name(e.getName()).profilePic(e.getProfilePic())
            .build();
    }
    private UserEntity toEntity(User d) {
        return UserEntity.builder()
            .id(d.getId()).googleId(d.getGoogleId())
            .email(d.getEmail()).name(d.getName()).profilePic(d.getProfilePic())
            .build();
    }

    @Override public Optional<User> findByGoogleId(String googleId) {
        return jpa.findByGoogleId(googleId).map(this::toDomain);
    }
    @Override public Optional<User> findById(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }
    @Override public User save(User user) {
        var saved = jpa.save(toEntity(user));
        return toDomain(saved);
    }

    
}
