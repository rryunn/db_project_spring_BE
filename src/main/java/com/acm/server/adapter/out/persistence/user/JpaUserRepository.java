package com.acm.server.adapter.out.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.acm.server.adapter.out.entity.UserEntity;

public interface JpaUserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByUserId(String userId);
}
