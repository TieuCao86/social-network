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

        String email = request.getEmail().trim().toLowerCase();
        String username = request.getUsername().trim();

        // 2. Check trùng lặp
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = userMapper.toEntity(request);
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        user = userRepository.saveAndFlush(user);

        UserProfile userProfile = UserProfile.builder()
                .userId(user.getId())
                .build();
        userProfileRepository.save(userProfile);

        UserSetting userSetting = UserSetting.builder()
                .userId(user.getId())
                .build();
        userSettingRepository.save(userSetting);

        return userMapper.toResponse(user);
    }

}
