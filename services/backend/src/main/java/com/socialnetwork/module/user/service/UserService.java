package com.socialnetwork.module.user.service;

import com.socialnetwork.module.user.dto.request.UserCreateRequest;
import com.socialnetwork.module.user.dto.response.UserResponse;

public interface UserService {

    UserResponse createUser(UserCreateRequest request);
}