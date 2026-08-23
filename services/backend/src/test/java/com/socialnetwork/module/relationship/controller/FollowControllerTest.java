package com.socialnetwork.module.relationship.controller;

import com.socialnetwork.module.relationship.entity.Follow;
import com.socialnetwork.module.relationship.repository.FollowRepository;
import com.socialnetwork.common.security.CustomUserDetails;
import com.socialnetwork.module.user.entity.User;
import com.socialnetwork.module.user.entity.UserRole;
import com.socialnetwork.module.user.entity.UserStatus;
import com.socialnetwork.module.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FollowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FollowRepository followRepository;

    private User userA;
    private User userB;
    private User userC;

    private CustomUserDetails userADetails;
    private CustomUserDetails userBDetails;

    @BeforeEach
    void setUp() {

        followRepository.deleteAll();
        userRepository.deleteAll();

        userA = createUser(
                "follow_a",
                "follow_a@example.com"
        );

        userB = createUser(
                "follow_b",
                "follow_b@example.com"
        );

        userC = createUser(
                "follow_c",
                "follow_c@example.com"
        );

        userADetails = new CustomUserDetails(userA);
        userBDetails = new CustomUserDetails(userB);
    }

    private User createUser(
            String username,
            String email
    ) {
        User user = User.builder()
                .username(username)
                .email(email)
                .passwordHash("hashed_password")
                .status(UserStatus.ACTIVE)
                .role(UserRole.USER)
                .build();

        return userRepository.saveAndFlush(user);
    }

    // ============================================================
    // FOLLOW
    // ============================================================

    @Test
    @DisplayName("POST /api/relationships/follows/{userId} - Follow thành công")
    void follow_Success() throws Exception {

        mockMvc.perform(
                        post("/api/relationships/follows/{userId}", userB.getId())
                                .with(user(userADetails))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.followerId")
                        .value(userA.getId().toString()))
                .andExpect(jsonPath("$.data.followingId")
                        .value(userB.getId().toString()));

        assertTrue(
                followRepository.existsByFollowerIdAndFollowingId(
                        userA.getId(),
                        userB.getId()
                )
        );
    }

    // ============================================================
    // FOLLOW SELF
    // ============================================================

    @Test
    @DisplayName("POST /api/relationships/follows/{userId} - Không thể follow chính mình")
    void follow_Self_ShouldReturn400() throws Exception {

        mockMvc.perform(
                        post("/api/relationships/follows/{userId}", userA.getId())
                                .with(user(userADetails))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("RELATIONSHIP_003"))
                .andExpect(jsonPath("$.message")
                        .value("Không thể theo dõi chính mình."));
    }

    @Test
    @DisplayName("DELETE /api/relationships/follows/{userId} - Không thể unfollow chính mình")
    void unfollow_Self_ShouldReturn400() throws Exception {

        mockMvc.perform(
                        delete("/api/relationships/follows/{userId}", userA.getId())
                                .with(user(userADetails))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ============================================================
    // FOLLOW DUPLICATE
    // ============================================================

    @Test
    @DisplayName("POST /api/relationships/follows/{userId} - Follow trùng")
    void follow_AlreadyFollowing_ShouldReturn409() throws Exception {

        createFollow(userA, userB);

        mockMvc.perform(
                        post("/api/relationships/follows/{userId}", userB.getId())
                                .with(user(userADetails))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ============================================================
    // UNFOLLOW
    // ============================================================

    @Test
    @DisplayName("DELETE /api/relationships/follows/{userId} - Unfollow thành công")
    void unfollow_Success() throws Exception {

        Follow follow = createFollow(userA, userB);

        mockMvc.perform(
                        delete("/api/relationships/follows/{userId}", userB.getId())
                                .with(user(userADetails))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message")
                        .value("Đã bỏ theo dõi người dùng."));

        assertFalse(
                followRepository.findById(follow.getId()).isPresent()
        );
    }

    // ============================================================
    // UNFOLLOW NOT FOLLOWING
    // ============================================================

    @Test
    @DisplayName("DELETE /api/relationships/follows/{userId} - Chưa follow")
    void unfollow_NotFollowing_ShouldReturn400() throws Exception {

        mockMvc.perform(
                        delete("/api/relationships/follows/{userId}", userB.getId())
                                .with(user(userADetails))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ============================================================
    // CHECK STATUS
    // ============================================================

    @Test
    @DisplayName("GET /api/relationships/follows/{userId}/status - Đang follow")
    void isFollowing_True() throws Exception {

        createFollow(userA, userB);

        mockMvc.perform(
                        get("/api/relationships/follows/{userId}/status", userB.getId())
                                .with(user(userADetails))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("GET /api/relationships/follows/{userId}/status - Chưa follow")
    void isFollowing_False() throws Exception {

        mockMvc.perform(
                        get("/api/relationships/follows/{userId}/status", userB.getId())
                                .with(user(userADetails))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(false));
    }

    // ============================================================
    // FOLLOWING
    // ============================================================

    @Test
    @DisplayName("GET /api/relationships/follows/following - Danh sách đang follow")
    void getFollowing_Success() throws Exception {

        createFollow(userA, userB);
        createFollow(userA, userC);

        mockMvc.perform(
                        get("/api/relationships/follows/following")
                                .with(user(userADetails))
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(2)))
                .andExpect(jsonPath("$.data.totalElements").value(2));
    }

    // ============================================================
    // FOLLOWERS
    // ============================================================

    @Test
    @DisplayName("GET /api/relationships/follows/followers - Danh sách follower")
    void getFollowers_Success() throws Exception {

        createFollow(userA, userB);
        createFollow(userC, userB);

        mockMvc.perform(
                        get("/api/relationships/follows/followers")
                                .with(user(userBDetails))
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(2)))
                .andExpect(jsonPath("$.data.totalElements").value(2));
    }

    // ============================================================
    // COUNT FOLLOWING
    // ============================================================

    @Test
    @DisplayName("GET /api/relationships/follows/following/count - Đếm following")
    void countFollowing_Success() throws Exception {

        createFollow(userA, userB);
        createFollow(userA, userC);

        mockMvc.perform(
                        get("/api/relationships/follows/following/count")
                                .with(user(userADetails))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(2));
    }

    // ============================================================
    // COUNT FOLLOWERS
    // ============================================================

    @Test
    @DisplayName("GET /api/relationships/follows/followers/count - Đếm followers")
    void countFollowers_Success() throws Exception {

        createFollow(userA, userB);
        createFollow(userC, userB);

        mockMvc.perform(
                        get("/api/relationships/follows/followers/count")
                                .with(user(userBDetails))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(2));
    }

    // ============================================================
    // UNAUTHENTICATED
    // ============================================================

    @Test
    @DisplayName("POST /api/relationships/follows/{userId} - Chưa đăng nhập")
    void follow_Unauthenticated_ShouldReturn401() throws Exception {

        mockMvc.perform(
                        post("/api/relationships/follows/{userId}", userB.getId())
                )
                .andExpect(status().isUnauthorized());
    }

    // ============================================================
    // HELPER
    // ============================================================

    private Follow createFollow(
            User follower,
            User following
    ) {
        Follow follow = Follow.builder()
                .followerId(follower.getId())
                .followingId(following.getId())
                .build();

        return followRepository.saveAndFlush(follow);
    }
}