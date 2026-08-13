package com.socialnetwork.module.user.repository;

import com.socialnetwork.module.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmailOrPhone(
            String email,
            String phone
    );

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByEmailOrPhone(String email, String phone);

    boolean existsByUsernameOrEmailOrPhone(String username, String email, String phone);
}