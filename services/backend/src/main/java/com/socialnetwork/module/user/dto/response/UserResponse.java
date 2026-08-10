package com.socialnetwork.module.user.dto.response;

import com.socialnetwork.module.user.entity.UserStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class UserResponse {

    private UUID id;

    private String username;

    private String email;

    private String phone;

    private UserStatus status;

    private boolean emailVerified;

    private boolean phoneVerified;

    private LocalDateTime createdAt;
}