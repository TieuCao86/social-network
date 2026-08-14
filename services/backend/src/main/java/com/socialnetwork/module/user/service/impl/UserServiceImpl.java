package com.socialnetwork.module.user.service.impl;

import com.socialnetwork.common.exception.BusinessException;
import com.socialnetwork.common.exception.ErrorCode;
import com.socialnetwork.module.user.dto.request.UserCreateRequest;
import com.socialnetwork.module.user.dto.response.UserResponse;
import com.socialnetwork.module.user.entity.User;
import com.socialnetwork.module.user.entity.UserProfile;
import com.socialnetwork.module.user.entity.UserSetting;
import com.socialnetwork.module.user.mapper.UserMapper;
import com.socialnetwork.module.user.repository.UserProfileRepository;
import com.socialnetwork.module.user.repository.UserRepository;
import com.socialnetwork.module.user.repository.UserSettingRepository;
import com.socialnetwork.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserSettingRepository userSettingRepository;

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {

        String email = request.getEmail() != null
                ? request.getEmail().trim().toLowerCase()
                : null;

        String phone = request.getPhone() != null
                ? request.getPhone().trim()
                : null;

        String username = request.getUsername().trim();

        if (email != null && email.isBlank()) {
            email = null;
        }

        if (phone != null && phone.isBlank()) {
            phone = null;
        }

        // Email hoặc Phone bắt buộc phải có một
        if (email == null && phone == null) {
            throw new BusinessException(
                    ErrorCode.EMAIL_OR_PHONE_REQUIRED
            );
        }

        // Kiểm tra Username
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException(
                    ErrorCode.USERNAME_ALREADY_EXISTS
            );
        }

        // Kiểm tra Email
        if (email != null && userRepository.existsByEmail(email)) {
            throw new BusinessException(
                    ErrorCode.EMAIL_ALREADY_EXISTS
            );
        }

        // Kiểm tra Phone
        if (phone != null && userRepository.existsByPhone(phone)) {
            throw new BusinessException(
                    ErrorCode.PHONE_ALREADY_EXISTS
            );
        }

        User user = userMapper.toEntity(request);

        user.setUsername(username);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );

        // Lưu User
        user = userRepository.saveAndFlush(user);

        // Tạo Profile
        userProfileRepository.save(
                UserProfile.builder()
                        .userId(user.getId())
                        .build()
        );

        // Tạo Setting
        userSettingRepository.save(
                UserSetting.builder()
                        .userId(user.getId())
                        .build()
        );

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUserProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toResponse(user);
    }
}