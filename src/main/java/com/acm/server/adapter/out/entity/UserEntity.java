package com.acm.server.adapter.out.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_name", unique = true)
    @Convert(converter = SimpleCryptoConverter.class)
    private String name;

    @Column(name="user_email", unique =true)
    @Convert(converter = SimpleCryptoConverter.class)
    private String email;

    @Column(name = "profile_picture")
    @Convert(converter = SimpleCryptoConverter.class)
    private String profilePic;

    @Column(name="google_id", unique =true)
    @Convert(converter = SimpleCryptoConverter.class)
    private String googleId;
}
