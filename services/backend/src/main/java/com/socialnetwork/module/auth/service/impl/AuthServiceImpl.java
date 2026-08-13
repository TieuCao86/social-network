package com.socialnetwork.module.auth.service.impl;

import com.socialnetwork.common.exception.BusinessException;
import com.socialnetwork.common.exception.ErrorCode;
import com.socialnetwork.common.security.JwtTokenProvider;
import com.socialnetwork.module.auth.dto.request.LoginRequest;
import com.socialnetwork.module.auth.dto.response.AuthResult;
import com.socialnetwork.module.auth.service.AuthService;
import com.socialnetwork.module.user.dto.request.UserCreateRequest;
import com.socialnetwork.module.user.dto.response.UserResponse;
import com.socialnetwork.module.user.entity.User;
import com.socialnetwork.module.user.entity.UserStatus;
import com.socialnetwork.module.user.repository.UserRepository;
import com.socialnetwork.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserService userService;

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public AuthResult login(LoginRequest request) {

        User user = userRepository
                .findByEmailOrPhone(
                        request.getPhoneOrEmail(),
                        request.getPhoneOrEmail()
                )
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.INVALID_CREDENTIALS
                        )
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        )) {
            throw new BusinessException(
                    ErrorCode.INVALID_CREDENTIALS
            );
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException(
                    ErrorCode.USER_NOT_ACTIVE
            );
        }

        String accessToken = jwtTokenProvider.generateToken(
                user.getId(),
                user.getUsername()
        );

        return AuthResult.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .accessToken(accessToken)
                .build();
    }

    @Override
    public UserResponse register(UserCreateRequest request) {

        return userService.createUser(request);
    }
}