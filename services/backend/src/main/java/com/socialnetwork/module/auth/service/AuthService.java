package com.socialnetwork.module.auth.service;

import com.socialnetwork.module.auth.dto.request.LoginRequest;
import com.socialnetwork.module.auth.dto.response.AuthResult;
import com.socialnetwork.module.user.dto.request.UserCreateRequest;
import com.socialnetwork.module.user.dto.response.UserResponse;

public interface AuthService {

    AuthResult login(LoginRequest request);

    UserResponse register(UserCreateRequest request);
}