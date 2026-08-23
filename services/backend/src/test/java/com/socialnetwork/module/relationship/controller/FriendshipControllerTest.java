package com.socialnetwork.module.relationship.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialnetwork.common.security.CustomUserDetails;
import com.socialnetwork.module.relationship.entity.Friendship;
import com.socialnetwork.module.relationship.entity.enums.FriendshipStatus;
import com.socialnetwork.module.relationship.repository.FriendshipRepository;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FriendshipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FriendshipRepository friendshipRepository;

    private User user1;
    private User user2;
    private User user3;

    private CustomUserDetails user1Details;
    private CustomUserDetails user2Details;
    private CustomUserDetails user3Details;

    @BeforeEach
    void setUp() {

        friendshipRepository.deleteAll();
        userRepository.deleteAll();

        // =======================================================
        // USER 1
        // =======================================================

        user1 = User.builder()
                .username("user_one")
                .email("user1@example.com")
                .passwordHash("hashed_password")
                .status(UserStatus.ACTIVE)
                .role(UserRole.USER)
                .build();

        user1 = userRepository.saveAndFlush(user1);
        user1Details = new CustomUserDetails(user1);

        // =======================================================
        // USER 2
        // =======================================================

        user2 = User.builder()
                .username("user_two")
                .email("user2@example.com")
                .passwordHash("hashed_password")
                .status(UserStatus.ACTIVE)
                .role(UserRole.USER)
                .build();

        user2 = userRepository.saveAndFlush(user2);
        user2Details = new CustomUserDetails(user2);

        // =======================================================
        // USER 3
        // =======================================================

        user3 = User.builder()
                .username("user_three")
                .email("user3@example.com")
                .passwordHash("hashed_password")
                .status(UserStatus.ACTIVE)
                .role(UserRole.USER)
                .build();

        user3 = userRepository.saveAndFlush(user3);
        user3Details = new CustomUserDetails(user3);
    }

    // =======================================================
    // 1. SEND FRIEND REQUEST
    // =======================================================

    @Test
    @DisplayName("POST /api/relationships/friends/{userId} - Gửi lời mời kết bạn thành công")
    void sendRequest_Success() throws Exception {

        mockMvc.perform(
                        post("/api/relationships/friends/{userId}", user2.getId())
                                .with(user(user1Details))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.requesterId")
                        .value(user1.getId().toString()))
                .andExpect(jsonPath("$.data.addresseeId")
                        .value(user2.getId().toString()))
                .andExpect(jsonPath("$.data.status")
                        .value("PENDING"));

        Friendship friendship = friendshipRepository
                .findBetween(user1.getId(), user2.getId())
                .orElseThrow();

        assertEquals(
                FriendshipStatus.PENDING,
                friendship.getStatus()
        );
    }

    // =======================================================
    // 2. SEND REQUEST - SELF
    // =======================================================

    @Test
    @DisplayName("POST /api/relationships/friends/{userId} - Không thể tự kết bạn")
    void sendRequest_Self_ShouldReturn400() throws Exception {

        mockMvc.perform(
                        post("/api/relationships/friends/{userId}", user1.getId())
                                .with(user(user1Details))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // =======================================================
    // 3. SEND REQUEST - ALREADY SENT
    // =======================================================

    @Test
    @DisplayName("POST /api/relationships/friends/{userId} - Đã gửi lời mời trước đó")
    void sendRequest_AlreadySent_ShouldReturnConflict() throws Exception {

        friendshipRepository.saveAndFlush(
                Friendship.builder()
                        .requesterId(user1.getId())
                        .addresseeId(user2.getId())
                        .status(FriendshipStatus.PENDING)
                        .build()
        );

        mockMvc.perform(
                        post("/api/relationships/friends/{userId}", user2.getId())
                                .with(user(user1Details))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    // =======================================================
    // 4. SEND REQUEST - REQUEST RECEIVED
    // =======================================================

    @Test
    @DisplayName("POST /api/relationships/friends/{userId} - Người kia đã gửi lời mời")
    void sendRequest_RequestAlreadyReceived_ShouldReturnConflict() throws Exception {

        friendshipRepository.saveAndFlush(
                Friendship.builder()
                        .requesterId(user2.getId())
                        .addresseeId(user1.getId())
                        .status(FriendshipStatus.PENDING)
                        .build()
        );

        mockMvc.perform(
                        post("/api/relationships/friends/{userId}", user2.getId())
                                .with(user(user1Details))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }

    // =======================================================
    // 5. ACCEPT REQUEST
    // =======================================================

    @Test
    @DisplayName("POST /api/relationships/friends/{userId}/accept - Chấp nhận lời mời")
    void acceptRequest_Success() throws Exception {

        friendshipRepository.saveAndFlush(
                Friendship.builder()
                        .requesterId(user2.getId())
                        .addresseeId(user1.getId())
                        .status(FriendshipStatus.PENDING)
                        .build()
        );

        mockMvc.perform(
                        post("/api/relationships/friends/{userId}/accept", user2.getId())
                                .with(user(user1Details))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status")
                        .value("ACCEPTED"));

        Friendship friendship = friendshipRepository
                .findBetween(user1.getId(), user2.getId())
                .orElseThrow();

        assertEquals(
                FriendshipStatus.ACCEPTED,
                friendship.getStatus()
        );
    }

    // =======================================================
    // 6. ACCEPT REQUEST - NOT FOUND
    // =======================================================

    @Test
    @DisplayName("POST /api/relationships/friends/{userId}/accept - Không tìm thấy lời mời")
    void acceptRequest_NotFound_ShouldReturn404() throws Exception {

        mockMvc.perform(
                        post("/api/relationships/friends/{userId}/accept", user2.getId())
                                .with(user(user1Details))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    // =======================================================
    // 7. REJECT REQUEST
    // =======================================================

    @Test
    @DisplayName("DELETE /api/relationships/friends/{userId}/request - Từ chối lời mời")
    void rejectRequest_Success() throws Exception {

        friendshipRepository.saveAndFlush(
                Friendship.builder()
                        .requesterId(user2.getId())
                        .addresseeId(user1.getId())
                        .status(FriendshipStatus.PENDING)
                        .build()
        );

        mockMvc.perform(
                        delete(
                                "/api/relationships/friends/{userId}/request",
                                user2.getId()
                        )
                                .with(user(user1Details))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        assertTrue(
                friendshipRepository
                        .findBetween(user1.getId(), user2.getId())
                        .isEmpty()
        );
    }

    // =======================================================
    // 8. CANCEL REQUEST
    // =======================================================

    @Test
    @DisplayName("DELETE /api/relationships/friends/{userId}/cancel - Hủy lời mời đã gửi")
    void cancelRequest_Success() throws Exception {

        friendshipRepository.saveAndFlush(
                Friendship.builder()
                        .requesterId(user1.getId())
                        .addresseeId(user2.getId())
                        .status(FriendshipStatus.PENDING)
                        .build()
        );

        mockMvc.perform(
                        delete(
                                "/api/relationships/friends/{userId}/cancel",
                                user2.getId()
                        )
                                .with(user(user1Details))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        assertTrue(
                friendshipRepository
                        .findBetween(user1.getId(), user2.getId())
                        .isEmpty()
        );
    }

    // =======================================================
    // 9. UNFRIEND
    // =======================================================

    @Test
    @DisplayName("DELETE /api/relationships/friends/{userId} - Hủy kết bạn")
    void unfriend_Success() throws Exception {

        friendshipRepository.saveAndFlush(
                Friendship.builder()
                        .requesterId(user1.getId())
                        .addresseeId(user2.getId())
                        .status(FriendshipStatus.ACCEPTED)
                        .build()
        );

        mockMvc.perform(
                        delete(
                                "/api/relationships/friends/{userId}",
                                user2.getId()
                        )
                                .with(user(user1Details))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        assertTrue(
                friendshipRepository
                        .findBetween(user1.getId(), user2.getId())
                        .isEmpty()
        );
    }

    // =======================================================
    // 10. GET RELATIONSHIP - NONE
    // =======================================================

    @Test
    @DisplayName("GET /api/relationships/{userId} - Chưa có quan hệ")
    void getRelationship_None() throws Exception {

        mockMvc.perform(
                        get("/api/relationships/{userId}", user2.getId())
                                .with(user(user1Details))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId")
                        .value(user2.getId().toString()))
                .andExpect(jsonPath("$.data.relationshipStatus")
                        .value("NONE"));
    }

    // =======================================================
    // 11. GET RELATIONSHIP - REQUEST SENT
    // =======================================================

    @Test
    @DisplayName("GET /api/relationships/{userId} - Mình đã gửi lời mời")
    void getRelationship_RequestSent() throws Exception {

        friendshipRepository.saveAndFlush(
                Friendship.builder()
                        .requesterId(user1.getId())
                        .addresseeId(user2.getId())
                        .status(FriendshipStatus.PENDING)
                        .build()
        );

        mockMvc.perform(
                        get("/api/relationships/{userId}", user2.getId())
                                .with(user(user1Details))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.relationshipStatus")
                        .value("REQUEST_SENT"));
    }

    // =======================================================
    // 12. GET RELATIONSHIP - REQUEST RECEIVED
    // =======================================================

    @Test
    @DisplayName("GET /api/relationships/{userId} - Người kia gửi lời mời")
    void getRelationship_RequestReceived() throws Exception {

        friendshipRepository.saveAndFlush(
                Friendship.builder()
                        .requesterId(user2.getId())
                        .addresseeId(user1.getId())
                        .status(FriendshipStatus.PENDING)
                        .build()
        );

        mockMvc.perform(
                        get("/api/relationships/{userId}", user2.getId())
                                .with(user(user1Details))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.relationshipStatus")
                        .value("REQUEST_RECEIVED"));
    }

    // =======================================================
    // 13. GET RELATIONSHIP - FRIENDS
    // =======================================================

    @Test
    @DisplayName("GET /api/relationships/{userId} - Đã là bạn bè")
    void getRelationship_Friends() throws Exception {

        friendshipRepository.saveAndFlush(
                Friendship.builder()
                        .requesterId(user1.getId())
                        .addresseeId(user2.getId())
                        .status(FriendshipStatus.ACCEPTED)
                        .build()
        );

        mockMvc.perform(
                        get("/api/relationships/{userId}", user2.getId())
                                .with(user(user1Details))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.relationshipStatus")
                        .value("FRIENDS"));
    }

    // =======================================================
    // 14. GET FRIENDS
    // =======================================================

    @Test
    @DisplayName("GET /api/relationships/friends - Lấy danh sách bạn bè")
    void getFriends_Success() throws Exception {

        friendshipRepository.saveAndFlush(
                Friendship.builder()
                        .requesterId(user1.getId())
                        .addresseeId(user2.getId())
                        .status(FriendshipStatus.ACCEPTED)
                        .build()
        );

        friendshipRepository.saveAndFlush(
                Friendship.builder()
                        .requesterId(user3.getId())
                        .addresseeId(user1.getId())
                        .status(FriendshipStatus.ACCEPTED)
                        .build()
        );

        mockMvc.perform(
                        get("/api/relationships/friends")
                                .param("page", "0")
                                .param("size", "10")
                                .with(user(user1Details))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(2)))
                .andExpect(jsonPath("$.data.totalElements").value(2));
    }

    // =======================================================
    // 15. GET RECEIVED REQUESTS
    // =======================================================

    @Test
    @DisplayName("GET /api/relationships/friends/requests/received - Lời mời nhận được")
    void getReceivedRequests_Success() throws Exception {

        friendshipRepository.saveAndFlush(
                Friendship.builder()
                        .requesterId(user2.getId())
                        .addresseeId(user1.getId())
                        .status(FriendshipStatus.PENDING)
                        .build()
        );

        friendshipRepository.saveAndFlush(
                Friendship.builder()
                        .requesterId(user3.getId())
                        .addresseeId(user1.getId())
                        .status(FriendshipStatus.PENDING)
                        .build()
        );

        mockMvc.perform(
                        get("/api/relationships/friends/requests/received")
                                .param("page", "0")
                                .param("size", "10")
                                .with(user(user1Details))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(2)))
                .andExpect(jsonPath("$.data.totalElements").value(2));
    }

    // =======================================================
    // 16. GET SENT REQUESTS
    // =======================================================

    @Test
    @DisplayName("GET /api/relationships/friends/requests/sent - Lời mời đã gửi")
    void getSentRequests_Success() throws Exception {

        friendshipRepository.saveAndFlush(
                Friendship.builder()
                        .requesterId(user1.getId())
                        .addresseeId(user2.getId())
                        .status(FriendshipStatus.PENDING)
                        .build()
        );

        friendshipRepository.saveAndFlush(
                Friendship.builder()
                        .requesterId(user1.getId())
                        .addresseeId(user3.getId())
                        .status(FriendshipStatus.PENDING)
                        .build()
        );

        mockMvc.perform(
                        get("/api/relationships/friends/requests/sent")
                                .param("page", "0")
                                .param("size", "10")
                                .with(user(user1Details))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(2)))
                .andExpect(jsonPath("$.data.totalElements").value(2));
    }

    // =======================================================
    // 17. COUNT FRIENDS
    // =======================================================

    @Test
    @DisplayName("GET /api/relationships/friends/count - Đếm số bạn bè")
    void countFriends_Success() throws Exception {

        friendshipRepository.saveAndFlush(
                Friendship.builder()
                        .requesterId(user1.getId())
                        .addresseeId(user2.getId())
                        .status(FriendshipStatus.ACCEPTED)
                        .build()
        );

        friendshipRepository.saveAndFlush(
                Friendship.builder()
                        .requesterId(user3.getId())
                        .addresseeId(user1.getId())
                        .status(FriendshipStatus.ACCEPTED)
                        .build()
        );

        mockMvc.perform(
                        get("/api/relationships/friends/count")
                                .with(user(user1Details))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(2));
    }

    // =======================================================
    // 18. UNAUTHENTICATED
    // =======================================================

    @Test
    @DisplayName("POST /api/relationships/friends/{userId} - Chưa đăng nhập")
    void sendRequest_Unauthenticated_ShouldReturn401() throws Exception {

        mockMvc.perform(
                        post(
                                "/api/relationships/friends/{userId}",
                                user2.getId()
                        )
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized());
    }
}