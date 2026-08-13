package com.socialnetwork.module.user.service.impl;

import com.socialnetwork.common.exception.BusinessException;
import com.socialnetwork.common.exception.ErrorCode;
import com.socialnetwork.module.user.dto.request.UserCreateRequest;
import com.socialnetwork.module.user.dto.response.UserResponse;
import com.socialnetwork.module.user.entity.User;
import com.socialnetwork.module.user.mapper.UserMapper;
import com.socialnetwork.module.user.repository.UserProfileRepository;
import com.socialnetwork.module.user.repository.UserRepository;
import com.socialnetwork.module.user.repository.UserSettingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserSettingRepository userSettingRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserCreateRequest request;

    @BeforeEach
    void setUp() {

        request = new UserCreateRequest();

        request.setUsername("new_user");
        request.setEmail("new@example.com");
        request.setPassword("Password123!");
    }

    // =========================================================
    // CREATE USER - SUCCESS
    // =========================================================

    @Test
    @DisplayName("createUser - Tạo User thành công")
    void createUser_Success() {

        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .username("new_user")
                .email("new@example.com")
                .passwordHash("encoded_password")
                .build();

        UserResponse expectedResponse = UserResponse.builder()
                .username("new_user")
                .email("new@example.com")
                .build();

        // Username chưa tồn tại
        when(userRepository.existsByUsername("new_user"))
                .thenReturn(false);

        // Email chưa tồn tại
        when(userRepository.existsByEmail("new@example.com"))
                .thenReturn(false);

        // Mapper
        when(userMapper.toEntity(request))
                .thenReturn(user);

        // Password
        when(passwordEncoder.encode("Password123!"))
                .thenReturn("encoded_password");

        // Save User
        when(userRepository.saveAndFlush(user))
                .thenReturn(user);

        // Response
        when(userMapper.toResponse(user))
                .thenReturn(expectedResponse);

        // Execute
        UserResponse result =
                userService.createUser(request);

        // Assert
        assertNotNull(result);

        assertEquals(
                "new_user",
                result.getUsername()
        );

        assertEquals(
                "new@example.com",
                result.getEmail()
        );

        // Verify
        verify(userRepository)
                .existsByUsername("new_user");

        verify(userRepository)
                .existsByEmail("new@example.com");

        verify(passwordEncoder)
                .encode("Password123!");

        verify(userRepository)
                .saveAndFlush(user);

        verify(userProfileRepository)
                .save(any());

        verify(userSettingRepository)
                .save(any());

        verify(userMapper)
                .toResponse(user);
    }

    // =========================================================
    // CREATE USER - MISSING EMAIL & PHONE
    // =========================================================

    @Test
    @DisplayName("createUser - Thiếu cả Email và Phone")
    void createUser_MissingEmailAndPhone() {

        request.setEmail(null);
        request.setPhone(null);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.createUser(request)
        );

        assertEquals(
                ErrorCode.EMAIL_OR_PHONE_REQUIRED,
                exception.getErrorCode()
        );

        verifyNoInteractions(userRepository);
        verifyNoInteractions(userMapper);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(userProfileRepository);
        verifyNoInteractions(userSettingRepository);
    }

    // =========================================================
    // CREATE USER - DUPLICATE USERNAME
    // =========================================================

    @Test
    @DisplayName("createUser - Username đã tồn tại")
    void createUser_DuplicateUsername() {

        when(userRepository.existsByUsername("new_user"))
                .thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.createUser(request)
        );

        assertEquals(
                ErrorCode.USERNAME_ALREADY_EXISTS,
                exception.getErrorCode()
        );

        verify(userRepository)
                .existsByUsername("new_user");

        verify(userRepository, never())
                .saveAndFlush(any());

        verifyNoInteractions(userMapper);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(userProfileRepository);
        verifyNoInteractions(userSettingRepository);
    }

    // =========================================================
    // CREATE USER - DUPLICATE EMAIL
    // =========================================================

    @Test
    @DisplayName("createUser - Email đã tồn tại")
    void createUser_DuplicateEmail() {

        when(userRepository.existsByUsername("new_user"))
                .thenReturn(false);

        when(userRepository.existsByEmail("new@example.com"))
                .thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.createUser(request)
        );

        assertEquals(
                ErrorCode.EMAIL_ALREADY_EXISTS,
                exception.getErrorCode()
        );

        verify(userRepository)
                .existsByUsername("new_user");

        verify(userRepository)
                .existsByEmail("new@example.com");

        verify(userRepository, never())
                .saveAndFlush(any());

        verifyNoInteractions(userMapper);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(userProfileRepository);
        verifyNoInteractions(userSettingRepository);
    }

    // =========================================================
    // CREATE USER - DUPLICATE PHONE
    // =========================================================

    @Test
    @DisplayName("createUser - Phone đã tồn tại")
    void createUser_DuplicatePhone() {

        request.setEmail(null);
        request.setPhone("0912345678");

        when(userRepository.existsByUsername("new_user"))
                .thenReturn(false);

        when(userRepository.existsByPhone("0912345678"))
                .thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> userService.createUser(request)
        );

        assertEquals(
                ErrorCode.PHONE_ALREADY_EXISTS,
                exception.getErrorCode()
        );

        verify(userRepository)
                .existsByUsername("new_user");

        verify(userRepository)
                .existsByPhone("0912345678");

        verify(userRepository, never())
                .saveAndFlush(any());

        verifyNoInteractions(userMapper);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(userProfileRepository);
        verifyNoInteractions(userSettingRepository);
    }

    // =========================================================
    // CREATE USER - EMAIL NORMALIZATION
    // =========================================================

    @Test
    @DisplayName("createUser - Chuẩn hóa Email")
    void createUser_NormalizeEmail() {

        request.setEmail("  NEW@EXAMPLE.COM  ");

        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        UserResponse response = UserResponse.builder()
                .username("new_user")
                .email("new@example.com")
                .build();

        when(userRepository.existsByUsername("new_user"))
                .thenReturn(false);

        when(userRepository.existsByEmail("new@example.com"))
                .thenReturn(false);

        when(userMapper.toEntity(request))
                .thenReturn(user);

        when(passwordEncoder.encode("Password123!"))
                .thenReturn("encoded_password");

        when(userRepository.saveAndFlush(user))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(response);

        UserResponse result =
                userService.createUser(request);

        assertEquals(
                "new@example.com",
                user.getEmail()
        );

        assertEquals(
                "new_user",
                user.getUsername()
        );

        assertEquals(
                "encoded_password",
                user.getPasswordHash()
        );

        assertEquals(
                "new@example.com",
                result.getEmail()
        );

        verify(userRepository)
                .existsByEmail("new@example.com");
    }

    // =========================================================
    // CREATE USER - PHONE NORMALIZATION
    // =========================================================

    @Test
    @DisplayName("createUser - Chuẩn hóa Phone")
    void createUser_NormalizePhone() {

        request.setEmail(null);
        request.setPhone("  0912345678  ");

        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        UserResponse response = UserResponse.builder()
                .username("new_user")
                .build();

        when(userRepository.existsByUsername("new_user"))
                .thenReturn(false);

        when(userRepository.existsByPhone("0912345678"))
                .thenReturn(false);

        when(userMapper.toEntity(request))
                .thenReturn(user);

        when(passwordEncoder.encode("Password123!"))
                .thenReturn("encoded_password");

        when(userRepository.saveAndFlush(user))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(response);

        userService.createUser(request);

        assertEquals(
                "0912345678",
                user.getPhone()
        );

        verify(userRepository)
                .existsByPhone("0912345678");
    }

    // =========================================================
    // CREATE USER - PASSWORD ENCODE
    // =========================================================

    @Test
    @DisplayName("createUser - Password được mã hóa")
    void createUser_PasswordEncoded() {

        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        UserResponse response = UserResponse.builder()
                .username("new_user")
                .build();

        when(userRepository.existsByUsername("new_user"))
                .thenReturn(false);

        when(userRepository.existsByEmail("new@example.com"))
                .thenReturn(false);

        when(userMapper.toEntity(request))
                .thenReturn(user);

        when(passwordEncoder.encode("Password123!"))
                .thenReturn("HASHED_PASSWORD");

        when(userRepository.saveAndFlush(user))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(response);

        userService.createUser(request);

        assertEquals(
                "HASHED_PASSWORD",
                user.getPasswordHash()
        );

        verify(passwordEncoder)
                .encode("Password123!");
    }

    // =========================================================
    // CREATE USER - EMAIL + PHONE
    // =========================================================

    @Test
    @DisplayName("createUser - Email và Phone đều được cung cấp")
    void createUser_EmailAndPhoneProvided() {

        request.setEmail("new@example.com");
        request.setPhone("0909999999");

        User user = User.builder()
                .id(UUID.randomUUID())
                .build();

        UserResponse response = UserResponse.builder()
                .username("new_user")
                .email("new@example.com")
                .build();

        when(userRepository.existsByUsername("new_user"))
                .thenReturn(false);

        when(userRepository.existsByEmail("new@example.com"))
                .thenReturn(false);

        when(userRepository.existsByPhone("0909999999"))
                .thenReturn(false);

        when(userMapper.toEntity(request))
                .thenReturn(user);

        when(passwordEncoder.encode("Password123!"))
                .thenReturn("encoded_password");

        when(userRepository.saveAndFlush(user))
                .thenReturn(user);

        when(userMapper.toResponse(user))
                .thenReturn(response);

        UserResponse result =
                userService.createUser(request);

        assertNotNull(result);

        verify(userRepository)
                .existsByUsername("new_user");

        verify(userRepository)
                .existsByEmail("new@example.com");

        verify(userRepository)
                .existsByPhone("0909999999");

        verify(userRepository)
                .saveAndFlush(user);

        verify(userProfileRepository)
                .save(any());

        verify(userSettingRepository)
                .save(any());
    }

    // =========================================================
    // GET CURRENT USER PROFILE
    // =========================================================

    @Test
    @DisplayName("getCurrentUserProfile - Lấy thông tin User thành công")
    void getCurrentUserProfile_Success() {

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("test_user")
                .email("test@example.com")
                .build();

        UserResponse expectedResponse = UserResponse.builder()
                .username("test_user")
                .email("test@example.com")
                .build();

        when(userMapper.toResponse(user))
                .thenReturn(expectedResponse);

        UserResponse result =
                userService.getCurrentUserProfile(user);

        assertNotNull(result);

        assertEquals(
                "test_user",
                result.getUsername()
        );

        assertEquals(
                "test@example.com",
                result.getEmail()
        );

        verify(userMapper)
                .toResponse(user);
    }
}