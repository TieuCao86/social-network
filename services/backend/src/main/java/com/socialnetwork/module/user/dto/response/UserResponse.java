package com.socialnetwork.module.user.dto.response;

import com.socialnetwork.module.user.entity.UserRole;
import com.socialnetwork.module.user.entity.UserStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private UUID userId;
    private String username;
    private String email;
    private String phone;

    private UserStatus status;
    private UserRole role;

    private boolean emailVerified;
    private boolean phoneVerified;

    // Profile
    private String fullName;
    private UUID avatarFileId;
    private UUID coverFileId;
    private String bio;
    private String website;
    private String location;
    private LocalDate birthDate;
}