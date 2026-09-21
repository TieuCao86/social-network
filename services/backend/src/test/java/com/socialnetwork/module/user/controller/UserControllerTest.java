package com.socialnetwork.module.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialnetwork.common.security.CustomUserDetails;
import com.socialnetwork.module.user.dto.request.UserProfileUpdateRequest;
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
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

        // Đổi sang stub bằng UUID: user.getId()
        when(userService.getCurrentUserProfile(user.getId()))
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
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("test_user"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.phone").value("0912345678"));

        // Verify với UUID: user.getId()
        verify(userService).getCurrentUserProfile(user.getId());
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

        // Đổi sang stub bằng UUID: user.getId()
        when(userService.getCurrentUserProfile(user.getId()))
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
                .andExpect(jsonPath("$.data.username").value("test_user"));

        // Verify với UUID: user.getId()
        verify(userService).getCurrentUserProfile(user.getId());
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

        // Đổi sang stub bằng UUID: user.getId()
        when(userService.getCurrentUserProfile(user.getId()))
                .thenThrow(new RuntimeException("Unexpected error"));

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

        // Verify với UUID: user.getId()
        verify(userService).getCurrentUserProfile(user.getId());
    }

    @Test
    @DisplayName("GET /api/users/profile - Trả đầy đủ thông tin Profile")
    void getProfile_WithProfileData_Return200() throws Exception {

        UserResponse response = UserResponse.builder()
                .userId(user.getId())
                .username("test_user")
                .email("test@example.com")
                .phone("0912345678")
                .status(UserStatus.ACTIVE)
                .role(UserRole.USER)
                .emailVerified(true)
                .phoneVerified(false)
                .fullName("Test User")
                .bio("Xin chào mọi người")
                .website("https://example.com")
                .location("Cao Lãnh")
                .birthDate(java.time.LocalDate.of(2005, 1, 1))
                .build();

        when(userService.getCurrentUserProfile(user.getId()))
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
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("test_user"))
                .andExpect(jsonPath("$.data.fullName").value("Test User"))
                .andExpect(jsonPath("$.data.bio").value("Xin chào mọi người"))
                .andExpect(jsonPath("$.data.website").value("https://example.com"))
                .andExpect(jsonPath("$.data.location").value("Cao Lãnh"))
                .andExpect(jsonPath("$.data.birthDate").value("2005-01-01"));

        verify(userService).getCurrentUserProfile(user.getId());
    }

    @Test
    @DisplayName("PUT /api/users/profile - Cập nhật Profile thành công")
    void updateProfile_Success() throws Exception {

        UserProfileUpdateRequest request = UserProfileUpdateRequest.builder()
                .fullName("Cao Quốc Trung")
                .bio("Xin chào mọi người!")
                .website("https://example.com")
                .location("Cao Lãnh")
                .birthDate(java.time.LocalDate.of(2005, 1, 1))
                .build();

        UserResponse response = UserResponse.builder()
                .userId(user.getId())
                .username("test_user")
                .email("test@example.com")
                .phone("0912345678")
                .status(UserStatus.ACTIVE)
                .role(UserRole.USER)
                .fullName("Cao Quốc Trung")
                .bio("Xin chào mọi người!")
                .website("https://example.com")
                .location("Cao Lãnh")
                .birthDate(java.time.LocalDate.of(2005, 1, 1))
                .build();

        when(userService.updateProfile(
                eq(user.getId()),
                any(UserProfileUpdateRequest.class)
        )).thenReturn(response);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        mockMvc.perform(
                        put("/api/users/profile")
                                .with(authentication(authentication))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("test_user"))
                .andExpect(jsonPath("$.data.fullName").value("Cao Quốc Trung"))
                .andExpect(jsonPath("$.data.bio").value("Xin chào mọi người!"))
                .andExpect(jsonPath("$.data.website").value("https://example.com"))
                .andExpect(jsonPath("$.data.location").value("Cao Lãnh"))
                .andExpect(jsonPath("$.data.birthDate").value("2005-01-01"));

        verify(userService).updateProfile(
                eq(user.getId()),
                any(UserProfileUpdateRequest.class)
        );
    }

    @Test
    @DisplayName("PUT /api/users/profile - Chưa đăng nhập trả về 401")
    void updateProfile_Unauthorized_Return401() throws Exception {

        UserProfileUpdateRequest request = UserProfileUpdateRequest.builder()
                .fullName("Cao Quốc Trung")
                .bio("Test")
                .build();

        mockMvc.perform(
                        put("/api/users/profile")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(userService);
    }

    @Test
    @DisplayName("PUT /api/users/profile - Service ném exception")
    void updateProfile_ServiceThrowsException() throws Exception {

        UserProfileUpdateRequest request = UserProfileUpdateRequest.builder()
                .fullName("Cao Quốc Trung")
                .bio("Test")
                .build();

        when(userService.updateProfile(
                eq(user.getId()),
                any(UserProfileUpdateRequest.class)
        )).thenThrow(new RuntimeException("Unexpected error"));

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        mockMvc.perform(
                        put("/api/users/profile")
                                .with(authentication(authentication))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isInternalServerError());

        verify(userService).updateProfile(
                eq(user.getId()),
                any(UserProfileUpdateRequest.class)
        );
    }

    @Test
    @DisplayName("PUT /api/users/profile - Nhận đúng dữ liệu từ request")
    void updateProfile_RequestBodyCorrect() throws Exception {

        UserProfileUpdateRequest request = UserProfileUpdateRequest.builder()
                .fullName("Cao Quốc Trung")
                .bio("Bio mới")
                .website("https://example.com")
                .location("Cao Lãnh")
                .birthDate(java.time.LocalDate.of(2005, 1, 1))
                .build();

        UserResponse response = UserResponse.builder()
                .userId(user.getId())
                .username("test_user")
                .fullName("Cao Quốc Trung")
                .bio("Bio mới")
                .website("https://example.com")
                .location("Cao Lãnh")
                .birthDate(java.time.LocalDate.of(2005, 1, 1))
                .build();

        when(userService.updateProfile(
                eq(user.getId()),
                any(UserProfileUpdateRequest.class)
        )).thenReturn(response);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        mockMvc.perform(
                        put("/api/users/profile")
                                .with(authentication(authentication))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.fullName")
                        .value("Cao Quốc Trung"));

        verify(userService).updateProfile(
                eq(user.getId()),
                argThat(actual ->
                        "Cao Quốc Trung".equals(actual.getFullName())
                                && "Bio mới".equals(actual.getBio())
                                && "https://example.com".equals(actual.getWebsite())
                                && "Cao Lãnh".equals(actual.getLocation())
                                && java.time.LocalDate.of(2005, 1, 1)
                                .equals(actual.getBirthDate())
                )
        );
    }
}