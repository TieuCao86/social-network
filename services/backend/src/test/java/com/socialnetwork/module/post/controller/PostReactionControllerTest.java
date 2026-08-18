package com.socialnetwork.module.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialnetwork.common.security.CustomUserDetails;
import com.socialnetwork.module.post.dto.request.ReactionRequest;
import com.socialnetwork.module.post.dto.response.ReactionResponse;
import com.socialnetwork.module.post.dto.response.ReactionUserResponse;
import com.socialnetwork.module.post.entity.enums.ReactionType;
import com.socialnetwork.module.post.service.PostReactionService;
import com.socialnetwork.module.user.entity.User;
import com.socialnetwork.module.user.entity.UserRole;
import com.socialnetwork.module.user.entity.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestExecutionListeners(
        listeners = WithSecurityContextTestExecutionListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
class PostReactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostReactionService postReactionService;

    private User user;

    private CustomUserDetails userDetails;

    private UUID postId;

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

        postId = UUID.randomUUID();
    }

    // =========================================================
    // 1. CREATE REACTION
    // =========================================================

    @Test
    @DisplayName("POST /api/posts/{id}/reactions - Tạo LIKE thành công")
    void toggleReaction_CreateReaction_Return200() throws Exception {

        ReactionResponse response = ReactionResponse.builder()
                .reacted(true)
                .currentUserReaction(ReactionType.LIKE)
                .totalReactions(1)
                .reactionCounts(
                        Map.of(
                                ReactionType.LIKE,
                                1L
                        )
                )
                .build();

        when(postReactionService.toggleReaction(
                eq(postId),
                eq(user.getId()),
                eq(ReactionType.LIKE)
        )).thenReturn(response);

        ReactionRequest request = ReactionRequest.builder()
                .type(ReactionType.LIKE)
                .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        mockMvc.perform(
                        post("/api/posts/{postId}/reactions", postId)
                                .with(authentication(authentication))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.reacted").value(true))
                .andExpect(jsonPath("$.data.currentUserReaction").value("LIKE"))
                .andExpect(jsonPath("$.data.totalReactions").value(1));

        verify(postReactionService)
                .toggleReaction(
                        postId,
                        user.getId(),
                        ReactionType.LIKE
                );
    }

    // =========================================================
    // 2. REMOVE REACTION
    // =========================================================

    @Test
    @DisplayName("POST /api/posts/{id}/reactions - Bấm lại reaction để hủy")
    void toggleReaction_RemoveReaction_Return200() throws Exception {

        ReactionResponse response = ReactionResponse.builder()
                .reacted(false)
                .currentUserReaction(null)
                .totalReactions(0)
                .reactionCounts(Map.of())
                .build();

        when(postReactionService.toggleReaction(
                eq(postId),
                eq(user.getId()),
                eq(ReactionType.LIKE)
        )).thenReturn(response);

        ReactionRequest request = ReactionRequest.builder()
                .type(ReactionType.LIKE)
                .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        mockMvc.perform(
                        post("/api/posts/{postId}/reactions", postId)
                                .with(authentication(authentication))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.reacted").value(false))
                .andExpect(jsonPath("$.data.totalReactions").value(0));

        verify(postReactionService)
                .toggleReaction(
                        postId,
                        user.getId(),
                        ReactionType.LIKE
                );
    }

    // =========================================================
    // 3. CHANGE REACTION
    // =========================================================

    @Test
    @DisplayName("POST /api/posts/{id}/reactions - Đổi LIKE thành LOVE")
    void toggleReaction_ChangeReaction_Return200() throws Exception {

        ReactionResponse response = ReactionResponse.builder()
                .reacted(true)
                .currentUserReaction(ReactionType.LOVE)
                .totalReactions(1)
                .reactionCounts(
                        Map.of(
                                ReactionType.LOVE,
                                1L
                        )
                )
                .build();

        when(postReactionService.toggleReaction(
                eq(postId),
                eq(user.getId()),
                eq(ReactionType.LOVE)
        )).thenReturn(response);

        ReactionRequest request = ReactionRequest.builder()
                .type(ReactionType.LOVE)
                .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        mockMvc.perform(
                        post("/api/posts/{postId}/reactions", postId)
                                .with(authentication(authentication))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.reacted").value(true))
                .andExpect(jsonPath("$.data.currentUserReaction").value("LOVE"))
                .andExpect(jsonPath("$.data.totalReactions").value(1));

        verify(postReactionService)
                .toggleReaction(
                        postId,
                        user.getId(),
                        ReactionType.LOVE
                );
    }

    // =========================================================
    // 4. GET REACTION USERS
    // =========================================================

    @Test
    @DisplayName("GET /api/posts/{id}/reactions - Lấy danh sách reaction")
    void getPostReactions_Return200() throws Exception {

        ReactionUserResponse reactionUser =
                ReactionUserResponse.builder()
                        .id(UUID.randomUUID())
                        .userId(user.getId())
                        .username("test_user")
                        .email("test@example.com")
                        .reactionType(ReactionType.LIKE)
                        .createdAt(Instant.now())
                        .build();

        PageImpl<ReactionUserResponse> page =
                new PageImpl<>(
                        List.of(reactionUser),
                        PageRequest.of(0, 10),
                        1
                );

        when(postReactionService.getPostReactions(
                eq(postId),
                isNull(),
                any()
        )).thenReturn(page);

        mockMvc.perform(
                        get("/api/posts/{postId}/reactions", postId)
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].username")
                        .value("test_user"))
                .andExpect(jsonPath("$.data.content[0].reactionType")
                        .value("LIKE"));

        verify(postReactionService)
                .getPostReactions(
                        eq(postId),
                        isNull(),
                        any()
                );
    }

    // =========================================================
    // 5. UNAUTHORIZED
    // =========================================================

    @Test
    @DisplayName("POST /api/posts/{id}/reactions - Chưa đăng nhập trả 401")
    void toggleReaction_Unauthorized_Return401() throws Exception {

        ReactionRequest request = ReactionRequest.builder()
                .type(ReactionType.LIKE)
                .build();

        mockMvc.perform(
                        post("/api/posts/{postId}/reactions", postId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(postReactionService);
    }
}