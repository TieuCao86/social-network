package com.socialnetwork.module.user.dto.response;

import com.socialnetwork.module.user.entity.UserRole;
import com.socialnetwork.module.user.entity.UserStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UserResponse {

    private UUID userId;

    private String username;

    private String email;

    private String phone;

    private UserStatus status;

    private UserRole role;

    private boolean emailVerified;

    private boolean phoneVerified;
}