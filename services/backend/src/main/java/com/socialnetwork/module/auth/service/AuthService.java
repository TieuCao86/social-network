package com.socialnetwork.module.auth.service;

import com.socialnetwork.module.auth.dto.request.LoginRequest;
import com.socialnetwork.module.auth.dto.response.AuthResult;

public interface AuthService {

    AuthResult login(LoginRequest request);
}