package com.socialnetwork.module.user.repository;

import com.socialnetwork.module.user.entity.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserSettingRepository
        extends JpaRepository<UserSetting, UUID> {

    Optional<UserSetting> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);
}