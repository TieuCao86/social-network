package com.socialnetwork.module.user.service;

import com.socialnetwork.module.user.dto.request.UserCreateRequest;
import com.socialnetwork.module.user.dto.request.UserProfileUpdateRequest;
import com.socialnetwork.module.user.dto.response.UserResponse;
import com.socialnetwork.module.user.dto.response.UserSearchResponse;
import com.socialnetwork.module.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    UserResponse createUser(UserCreateRequest request);

    UserResponse getCurrentUserProfile(UUID userId);

    Page<UserSearchResponse> searchUsers(
            UUID currentUserId,
            String keyword,
            Pageable pageable
    );

    UserResponse updateProfile(
            UUID userId,
            UserProfileUpdateRequest request
    );
}