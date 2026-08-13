package com.socialnetwork.module.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialnetwork.common.exception.ErrorCode;
import com.socialnetwork.module.auth.dto.request.LoginRequest;
import com.socialnetwork.module.user.dto.request.UserCreateRequest;
import com.socialnetwork.module.user.entity.User;
import com.socialnetwork.module.user.entity.UserStatus;
import com.socialnetwork.module.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {

        userRepository.deleteAll();

        User existingUser = User.builder()
                .username("existing_user")
                .email("existing@example.com")
                .phone("0912345678")
                .passwordHash(
                        passwordEncoder.encode("Password123!")
                )
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.saveAndFlush(existingUser);
    }

    @Test
    @DisplayName("POST /api/auth/register - Đăng ký thành công trả về 201")
    void register_Success() throws Exception {

        UserCreateRequest request = new UserCreateRequest();

        request.setUsername("brand_new_user");
        request.setEmail("newuser@example.com");
        request.setPassword("Password123!");

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.username")
                        .value("brand_new_user"));
    }

    @Test
    @DisplayName("POST /api/auth/register - Thiếu Email và Phone trả về 400")
    void register_MissingEmailAndPhone_Return400() throws Exception {

        UserCreateRequest request = new UserCreateRequest();

        request.setUsername("invalid_user");
        request.setPassword("Password123!");

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.EMAIL_OR_PHONE_REQUIRED.getCode()));
    }

    @Test
    @DisplayName("POST /api/auth/register - Email trùng trả về 409")
    void register_DuplicateEmail_Return409Conflict() throws Exception {

        UserCreateRequest request = new UserCreateRequest();

        request.setUsername("another_user");
        request.setEmail("existing@example.com");
        request.setPassword("Password123!");

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.EMAIL_ALREADY_EXISTS.getCode()));
    }

    @Test
    @DisplayName("POST /api/auth/register - Username trùng trả về 409")
    void register_DuplicateUsername_Return409Conflict() throws Exception {

        UserCreateRequest request = new UserCreateRequest();

        request.setUsername("existing_user");
        request.setEmail("unique@example.com");
        request.setPassword("Password123!");

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.USERNAME_ALREADY_EXISTS.getCode()));
    }

    // =========================================================
// LOGIN
// =========================================================

    @Test
    @DisplayName("POST /api/auth/login - Đăng nhập thành công bằng Email")
    void login_Success_ByEmail() throws Exception {

        LoginRequest request = new LoginRequest();

        request.setPhoneOrEmail("existing@example.com");
        request.setPassword("Password123!");

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())

                // ApiResponse
                .andExpect(jsonPath("$.success")
                        .value(true))

                // LoginResponse
                .andExpect(jsonPath("$.data.userId")
                        .isNotEmpty())
                .andExpect(jsonPath("$.data.username")
                        .value("existing_user"))

                // Cookie access_token
                .andExpect(header().string(
                        "Set-Cookie",
                        containsString("access_token=")
                ))
                .andExpect(header().string(
                        "Set-Cookie",
                        containsString("HttpOnly")
                ))
                .andExpect(header().string(
                        "Set-Cookie",
                        containsString("Path=/")
                ));
    }

    @Test
    @DisplayName("POST /api/auth/login - Đăng nhập thành công bằng Phone")
    void login_Success_ByPhone() throws Exception {

        LoginRequest request = new LoginRequest();

        request.setPhoneOrEmail("0912345678");
        request.setPassword("Password123!");

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success")
                        .value(true))
                .andExpect(jsonPath("$.data.userId")
                        .isNotEmpty())
                .andExpect(jsonPath("$.data.username")
                        .value("existing_user"))
                .andExpect(header().string(
                        "Set-Cookie",
                        containsString("access_token=")
                ))
                .andExpect(header().string(
                        "Set-Cookie",
                        containsString("HttpOnly")
                ));
    }

    @Test
    @DisplayName("POST /api/auth/login - Email hoặc Phone không tồn tại trả về 401")
    void login_UserNotFound_Return401() throws Exception {

        LoginRequest request = new LoginRequest();

        request.setPhoneOrEmail("notfound@example.com");
        request.setPassword("Password123!");

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success")
                        .value(false))
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.INVALID_CREDENTIALS.getCode()));
    }

    @Test
    @DisplayName("POST /api/auth/login - Sai mật khẩu trả về 401")
    void login_WrongPassword_Return401() throws Exception {

        LoginRequest request = new LoginRequest();

        request.setPhoneOrEmail("existing@example.com");
        request.setPassword("WrongPassword123!");

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success")
                        .value(false))
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.INVALID_CREDENTIALS.getCode()));
    }

    @Test
    @DisplayName("POST /api/auth/login - User không ACTIVE trả về 409")
    void login_UserNotActive_Return409() throws Exception {

        User user = userRepository
                .findByEmail("existing@example.com")
                .orElseThrow();

        user.setStatus(UserStatus.INACTIVE);

        userRepository.saveAndFlush(user);

        LoginRequest request = new LoginRequest();

        request.setPhoneOrEmail("existing@example.com");
        request.setPassword("Password123!");

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success")
                        .value(false))
                .andExpect(jsonPath("$.code")
                        .value(ErrorCode.USER_NOT_ACTIVE.getCode()));
    }
}