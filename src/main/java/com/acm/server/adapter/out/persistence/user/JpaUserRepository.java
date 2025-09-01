package com.acm.server.adapter.out.persistence.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.acm.server.adapter.out.entity.UserEntity;

public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByGoogleId(String googleId);
}
