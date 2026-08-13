package com.socialnetwork.module.user.entity;

import com.socialnetwork.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(unique = true)
    private String email;

    @Column(length = 20, unique = true)
    private String phone;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private UserRole role = UserRole.USER;

    @Column(nullable = false)
    private boolean emailVerified;

    @Column(nullable = false)
    private boolean phoneVerified;
}