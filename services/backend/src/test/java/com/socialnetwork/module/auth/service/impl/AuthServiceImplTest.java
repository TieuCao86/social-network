package com.socialnetwork.module.auth.service.impl;

import com.socialnetwork.common.exception.BusinessException;
import com.socialnetwork.common.exception.ErrorCode;
import com.socialnetwork.common.security.JwtTokenProvider;
import com.socialnetwork.module.auth.dto.request.LoginRequest;
import com.socialnetwork.module.auth.dto.response.AuthResult;
import com.socialnetwork.module.user.dto.request.UserCreateRequest;
import com.socialnetwork.module.user.dto.response.UserResponse;
import com.socialnetwork.module.user.entity.User;
import com.socialnetwork.module.user.entity.UserStatus;
import com.socialnetwork.module.user.repository.UserRepository;
import com.socialnetwork.module.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private User activeUser;

    @BeforeEach
    void setUp() {

        activeUser = User.builder()
                .id(UUID.randomUUID())
                .username("existing_user")
                .email("existing@example.com")
                .phone("0912345678")
                .passwordHash("encoded_password")
                .status(UserStatus.ACTIVE)
                .build();
    }

    // =========================================================
    // LOGIN - SUCCESS
    // =========================================================

    @Test
    @DisplayName("login - Đăng nhập thành công bằng Email")
    void login_Success_ByEmail() {

        LoginRequest request = new LoginRequest();

        request.setPhoneOrEmail("existing@example.com");
        request.setPassword("Password123!");

        when(userRepository.findByEmailOrPhone(
                "existing@example.com",
                "existing@example.com"
        )).thenReturn(Optional.of(activeUser));

        when(passwordEncoder.matches(
                "Password123!",
                "encoded_password"
        )).thenReturn(true);

        when(jwtTokenProvider.generateToken(
                activeUser.getId(),
                activeUser.getUsername()
        )).thenReturn("mock-jwt-token");

        AuthResult result = authService.login(request);

        assertNotNull(result);

        assertEquals(
                activeUser.getId(),
                result.getUserId()
        );

        assertEquals(
                "existing_user",
                result.getUsername()
        );

        assertEquals(
                "mock-jwt-token",
                result.getAccessToken()
        );

        verify(userRepository).findByEmailOrPhone(
                "existing@example.com",
                "existing@example.com"
        );

        verify(passwordEncoder).matches(
                "Password123!",
                "encoded_password"
        );

        verify(jwtTokenProvider).generateToken(
                activeUser.getId(),
                activeUser.getUsername()
        );
    }

    // =========================================================
    // LOGIN - PHONE
    // =========================================================

    @Test
    @DisplayName("login - Đăng nhập thành công bằng Phone")
    void login_Success_ByPhone() {

        LoginRequest request = new LoginRequest();

        request.setPhoneOrEmail("0912345678");
        request.setPassword("Password123!");

        when(userRepository.findByEmailOrPhone(
                "0912345678",
                "0912345678"
        )).thenReturn(Optional.of(activeUser));

        when(passwordEncoder.matches(
                "Password123!",
                "encoded_password"
        )).thenReturn(true);

        when(jwtTokenProvider.generateToken(
                activeUser.getId(),
                activeUser.getUsername()
        )).thenReturn("mock-jwt-token");

        AuthResult result = authService.login(request);

        assertNotNull(result);

        assertEquals(
                activeUser.getId(),
                result.getUserId()
        );

        assertEquals(
                "existing_user",
                result.getUsername()
        );

        assertEquals(
                "mock-jwt-token",
                result.getAccessToken()
        );

        verify(userRepository).findByEmailOrPhone(
                "0912345678",
                "0912345678"
        );
    }

    // =========================================================
    // LOGIN - USER NOT FOUND
    // =========================================================

    @Test
    @DisplayName("login - Không tìm thấy User")
    void login_UserNotFound() {

        LoginRequest request = new LoginRequest();

        request.setPhoneOrEmail("notfound@example.com");
        request.setPassword("Password123!");

        when(userRepository.findByEmailOrPhone(
                "notfound@example.com",
                "notfound@example.com"
        )).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> authService.login(request)
        );

        assertEquals(
                ErrorCode.INVALID_CREDENTIALS,
                exception.getErrorCode()
        );

        verify(userRepository).findByEmailOrPhone(
                "notfound@example.com",
                "notfound@example.com"
        );

        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtTokenProvider);
    }

    // =========================================================
    // LOGIN - WRONG PASSWORD
    // =========================================================

    @Test
    @DisplayName("login - Sai mật khẩu")
    void login_WrongPassword() {

        LoginRequest request = new LoginRequest();

        request.setPhoneOrEmail("existing@example.com");
        request.setPassword("WrongPassword!");

        when(userRepository.findByEmailOrPhone(
                "existing@example.com",
                "existing@example.com"
        )).thenReturn(Optional.of(activeUser));

        when(passwordEncoder.matches(
                "WrongPassword!",
                "encoded_password"
        )).thenReturn(false);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> authService.login(request)
        );

        assertEquals(
                ErrorCode.INVALID_CREDENTIALS,
                exception.getErrorCode()
        );

        verify(passwordEncoder).matches(
                "WrongPassword!",
                "encoded_password"
        );

        verifyNoInteractions(jwtTokenProvider);
    }

    // =========================================================
    // LOGIN - USER NOT ACTIVE
    // =========================================================

    @Test
    @DisplayName("login - User không ACTIVE")
    void login_UserNotActive() {

        User inactiveUser = User.builder()
                .id(UUID.randomUUID())
                .username("inactive_user")
                .email("inactive@example.com")
                .phone("0900000000")
                .passwordHash("encoded_password")
                .status(UserStatus.INACTIVE)
                .build();

        LoginRequest request = new LoginRequest();

        request.setPhoneOrEmail("inactive@example.com");
        request.setPassword("Password123!");

        when(userRepository.findByEmailOrPhone(
                "inactive@example.com",
                "inactive@example.com"
        )).thenReturn(Optional.of(inactiveUser));

        when(passwordEncoder.matches(
                "Password123!",
                "encoded_password"
        )).thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> authService.login(request)
        );

        assertEquals(
                ErrorCode.USER_NOT_ACTIVE,
                exception.getErrorCode()
        );

        verify(passwordEncoder).matches(
                "Password123!",
                "encoded_password"
        );

        verifyNoInteractions(jwtTokenProvider);
    }

    // =========================================================
    // REGISTER
    // =========================================================

    @Test
    @DisplayName("register - Gọi UserService tạo User thành công")
    void register_Success() {

        UserCreateRequest request = new UserCreateRequest();

        request.setUsername("new_user");
        request.setEmail("new@example.com");
        request.setPassword("Password123!");

        UserResponse expectedResponse = UserResponse.builder()
                .username("new_user")
                .email("new@example.com")
                .build();

        when(userService.createUser(request))
                .thenReturn(expectedResponse);

        UserResponse result =
                authService.register(request);

        assertNotNull(result);

        assertEquals(
                "new_user",
                result.getUsername()
        );

        assertEquals(
                "new@example.com",
                result.getEmail()
        );

        verify(userService)
                .createUser(request);

        verifyNoInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtTokenProvider);
    }
}