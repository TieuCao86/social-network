package com.socialnetwork.module.user.service;

import com.socialnetwork.module.user.dto.request.UserCreateRequest;
import com.socialnetwork.module.user.dto.response.UserResponse;
import com.socialnetwork.module.user.entity.User;

import java.util.UUID;

public interface UserService {

    UserResponse createUser(UserCreateRequest request);

    UserResponse getCurrentUserProfile(User user);
}