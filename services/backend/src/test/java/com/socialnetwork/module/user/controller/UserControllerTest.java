package com.socialnetwork.module.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialnetwork.common.security.CustomUserDetails;
import com.socialnetwork.module.user.dto.response.UserResponse;
import com.socialnetwork.module.user.entity.User;
import com.socialnetwork.module.user.entity.UserRole;
import com.socialnetwork.module.user.entity.UserStatus;
import com.socialnetwork.module.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestExecutionListeners(
        listeners = WithSecurityContextTestExecutionListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private User user;

    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .id(UUID.randomUUID())
                .username("test_user")
                .email("test@example.com")
                .phone("0912345678")
                .passwordHash("encoded_password")
                .status(UserStatus.ACTIVE)
                .role(UserRole.USER)
                .build();

        userDetails = new CustomUserDetails(user);
    }

    // =========================================================
    // GET PROFILE - SUCCESS
    // =========================================================

    @Test
    @DisplayName("GET /api/users/profile - Lấy profile thành công")
    void getProfile_Success() throws Exception {

        UserResponse response = UserResponse.builder()
                .userId(user.getId())
                .username("test_user")
                .email("test@example.com")
                .phone("0912345678")
                .build();

        when(userService.getCurrentUserProfile(user))
                .thenReturn(response);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        mockMvc.perform(
                        get("/api/users/profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(authentication(authentication))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success")
                        .value(true))
                .andExpect(jsonPath("$.data.username")
                        .value("test_user"))
                .andExpect(jsonPath("$.data.email")
                        .value("test@example.com"))
                .andExpect(jsonPath("$.data.phone")
                        .value("0912345678"));

        verify(userService)
                .getCurrentUserProfile(user);
    }

    // =========================================================
    // GET PROFILE - USER ROLE
    // =========================================================

    @Test
    @DisplayName("GET /api/users/profile - User có ROLE_USER được phép truy cập")
    void getProfile_UserRole_Return200() throws Exception {

        UserResponse response = UserResponse.builder()
                .userId(user.getId())
                .username("test_user")
                .email("test@example.com")
                .build();

        when(userService.getCurrentUserProfile(user))
                .thenReturn(response);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        mockMvc.perform(
                        get("/api/users/profile")
                                .with(authentication(authentication))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username")
                        .value("test_user"));

        verify(userService)
                .getCurrentUserProfile(user);
    }

    // =========================================================
    // GET PROFILE - UNAUTHORIZED
    // =========================================================

    @Test
    @DisplayName("GET /api/users/profile - Chưa đăng nhập trả về 401")
    void getProfile_Unauthorized_Return401() throws Exception {

        mockMvc.perform(
                        get("/api/users/profile")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(userService);
    }

    // =========================================================
    // GET PROFILE - SERVICE ERROR
    // =========================================================

    @Test
    @DisplayName("GET /api/users/profile - Service ném exception")
    void getProfile_ServiceThrowsException() throws Exception {

        when(userService.getCurrentUserProfile(user))
                .thenThrow(
                        new RuntimeException("Unexpected error")
                );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        mockMvc.perform(
                        get("/api/users/profile")
                                .with(authentication(authentication))
                )
                .andExpect(status().isInternalServerError());

        verify(userService)
                .getCurrentUserProfile(user);
    }
}