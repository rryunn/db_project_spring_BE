package com.acm.server.adapter.out.persistence.user;

import com.acm.server.adapter.out.entity.UserEntity;

public class UserPersistenceAdapter {
    //예시
    // private final JpaUserRepository jpaUserRepository;

    // @Override
    // public User findByUserId(String userId) {
    //     UserEntity userEntity = jpaUserRepository.findByUserId(userId);
    //     User user = new User();
    //     if(userEntity == null) return null;

    //     user = User.builder()
    //             .userSeq(userEntity.getUserSeq())
    //             .userId(userEntity.getUserId())
    //             .userPw(userEntity.getUserPw())
    //             .phoneNumber(userEntity.getPhoneNumber())
    //             .build();

    //     return user;
    // }
}
