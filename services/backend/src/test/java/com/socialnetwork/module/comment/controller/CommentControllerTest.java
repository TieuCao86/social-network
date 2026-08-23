package com.socialnetwork.module.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialnetwork.common.security.CustomUserDetails;
import com.socialnetwork.module.comment.dto.request.CreateCommentRequest;
import com.socialnetwork.module.comment.dto.request.UpdateCommentRequest;
import com.socialnetwork.module.comment.entity.Comment;
import com.socialnetwork.module.comment.entity.CommentStatus;
import com.socialnetwork.module.comment.repository.CommentRepository;
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

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    private User user1;
    private User user2;

    private CustomUserDetails user1Details;
    private CustomUserDetails user2Details;

    private UUID postId;

    @BeforeEach
    void setUp() {

        commentRepository.deleteAll();
        userRepository.deleteAll();

        postId = UUID.randomUUID();

        // ============================================================
        // USER 1
        // ============================================================

        user1 = User.builder()
                .username("comment_user1")
                .email("comment_user1@example.com")
                .passwordHash("hashed_password")
                .status(UserStatus.ACTIVE)
                .role(UserRole.USER)
                .build();

        user1 = userRepository.saveAndFlush(user1);
        user1Details = new CustomUserDetails(user1);

        // ============================================================
        // USER 2
        // ============================================================

        user2 = User.builder()
                .username("comment_user2")
                .email("comment_user2@example.com")
                .passwordHash("hashed_password")
                .status(UserStatus.ACTIVE)
                .role(UserRole.USER)
                .build();

        user2 = userRepository.saveAndFlush(user2);
        user2Details = new CustomUserDetails(user2);
    }

    // ============================================================
    // 1. CREATE COMMENT
    // ============================================================

    @Test
    @DisplayName("POST /api/comments/posts/{postId} - Tạo comment thành công")
    void createComment_Success() throws Exception {

        CreateCommentRequest request = CreateCommentRequest.builder()
                .content("Đây là một comment.")
                .build();

        mockMvc.perform(
                        post("/api/comments/posts/{postId}", postId)
                                .with(user(user1Details))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.postId")
                        .value(postId.toString()))
                .andExpect(jsonPath("$.data.userId")
                        .value(user1.getId().toString()))
                .andExpect(jsonPath("$.data.content")
                        .value("Đây là một comment."))
                .andExpect(jsonPath("$.data.status")
                        .value("ACTIVE"));

        assertEquals(
                1,
                commentRepository.countByPostIdAndStatus(
                        postId,
                        CommentStatus.ACTIVE
                )
        );
    }

    // ============================================================
    // 2. CREATE REPLY
    // ============================================================

    @Test
    @DisplayName("POST /api/comments/posts/{postId} - Tạo reply thành công")
    void createReply_Success() throws Exception {

        Comment parent = createComment(
                user1,
                postId,
                null,
                "Comment gốc"
        );

        CreateCommentRequest request = CreateCommentRequest.builder()
                .content("Đây là reply.")
                .parentId(parent.getId())
                .build();

        mockMvc.perform(
                        post("/api/comments/posts/{postId}", postId)
                                .with(user(user2Details))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.parentId")
                        .value(parent.getId().toString()))
                .andExpect(jsonPath("$.data.content")
                        .value("Đây là reply."));
    }

    // ============================================================
    // 3. CREATE COMMENT - BLANK
    // ============================================================

    @Test
    @DisplayName("POST /api/comments/posts/{postId} - Content rỗng")
    void createComment_BlankContent_ShouldReturn400() throws Exception {

        CreateCommentRequest request = CreateCommentRequest.builder()
                .content("   ")
                .build();

        mockMvc.perform(
                        post("/api/comments/posts/{postId}", postId)
                                .with(user(user1Details))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    // ============================================================
    // 4. GET COMMENTS
    // ============================================================

    @Test
    @DisplayName("GET /api/comments/posts/{postId} - Lấy danh sách comment")
    void getComments_Success() throws Exception {

        createComment(
                user1,
                postId,
                null,
                "Comment 1"
        );

        createComment(
                user2,
                postId,
                null,
                "Comment 2"
        );

        mockMvc.perform(
                        get("/api/comments/posts/{postId}", postId)
                                .with(user(user1Details))
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(2)))
                .andExpect(jsonPath("$.data.totalElements").value(2));
    }

    // ============================================================
    // 5. GET REPLIES
    // ============================================================

    @Test
    @DisplayName("GET /api/comments/{commentId}/replies - Lấy replies")
    void getReplies_Success() throws Exception {

        Comment parent = createComment(
                user1,
                postId,
                null,
                "Comment gốc"
        );

        createComment(
                user2,
                postId,
                parent.getId(),
                "Reply 1"
        );

        createComment(
                user1,
                postId,
                parent.getId(),
                "Reply 2"
        );

        mockMvc.perform(
                        get(
                                "/api/comments/{commentId}/replies",
                                parent.getId()
                        )
                                .with(user(user1Details))
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(2)))
                .andExpect(jsonPath("$.data.totalElements").value(2));
    }

    // ============================================================
    // 6. COUNT COMMENTS
    // ============================================================

    @Test
    @DisplayName("GET /api/comments/posts/{postId}/count - Đếm comment")
    void countComments_Success() throws Exception {

        createComment(
                user1,
                postId,
                null,
                "Comment 1"
        );

        createComment(
                user2,
                postId,
                null,
                "Comment 2"
        );

        mockMvc.perform(
                        get("/api/comments/posts/{postId}/count", postId)
                                .with(user(user1Details))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(2));
    }

    // ============================================================
    // 7. COUNT REPLIES
    // ============================================================

    @Test
    @DisplayName("GET /api/comments/{commentId}/replies/count - Đếm replies")
    void countReplies_Success() throws Exception {

        Comment parent = createComment(
                user1,
                postId,
                null,
                "Comment gốc"
        );

        createComment(
                user2,
                postId,
                parent.getId(),
                "Reply 1"
        );

        createComment(
                user1,
                postId,
                parent.getId(),
                "Reply 2"
        );

        mockMvc.perform(
                        get(
                                "/api/comments/{commentId}/replies/count",
                                parent.getId()
                        )
                                .with(user(user1Details))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(2));
    }

    // ============================================================
    // 8. UPDATE COMMENT
    // ============================================================

    @Test
    @DisplayName("PUT /api/comments/{commentId} - Cập nhật comment")
    void updateComment_Success() throws Exception {

        Comment comment = createComment(
                user1,
                postId,
                null,
                "Nội dung cũ"
        );

        UpdateCommentRequest request = UpdateCommentRequest.builder()
                .content("Nội dung mới")
                .build();

        mockMvc.perform(
                        put("/api/comments/{commentId}", comment.getId())
                                .with(user(user1Details))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content")
                        .value("Nội dung mới"));
    }

    // ============================================================
    // 9. DELETE COMMENT
    // ============================================================

    @Test
    @DisplayName("DELETE /api/comments/{commentId} - Xóa comment")
    void deleteComment_Success() throws Exception {

        Comment comment = createComment(
                user1,
                postId,
                null,
                "Comment cần xóa"
        );

        mockMvc.perform(
                        delete("/api/comments/{commentId}", comment.getId())
                                .with(user(user1Details))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        Comment deleted = commentRepository
                .findById(comment.getId())
                .orElseThrow();

        assertEquals(
                CommentStatus.DELETED,
                deleted.getStatus()
        );
    }

    // ============================================================
    // 10. UNAUTHENTICATED CREATE
    // ============================================================

    @Test
    @DisplayName("POST /api/comments/posts/{postId} - Chưa đăng nhập")
    void createComment_Unauthenticated_ShouldReturn401()
            throws Exception {

        CreateCommentRequest request = CreateCommentRequest.builder()
                .content("Test comment")
                .build();

        mockMvc.perform(
                        post("/api/comments/posts/{postId}", postId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isUnauthorized());
    }

    // ============================================================
    // 11. UPDATE COMMENT - OTHER USER
    // ============================================================

    @Test
    @DisplayName("PUT /api/comments/{commentId} - Người khác không được sửa")
    void updateComment_OtherUser_ShouldFail() throws Exception {

        Comment comment = createComment(
                user1,
                postId,
                null,
                "Nội dung cũ"
        );

        UpdateCommentRequest request = UpdateCommentRequest.builder()
                .content("Nội dung mới")
                .build();

        mockMvc.perform(
                        put("/api/comments/{commentId}", comment.getId())
                                .with(user(user2Details))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isForbidden());
    }

    // ============================================================
    // 12. DELETE COMMENT - OTHER USER
    // ============================================================

    @Test
    @DisplayName("DELETE /api/comments/{commentId} - Người khác không được xóa")
    void deleteComment_OtherUser_ShouldFail() throws Exception {

        Comment comment = createComment(
                user1,
                postId,
                null,
                "Comment của user1"
        );

        mockMvc.perform(
                        delete("/api/comments/{commentId}", comment.getId())
                                .with(user(user2Details))
                )
                .andExpect(status().isForbidden());
    }

    // ============================================================
    // HELPER
    // ============================================================

    private Comment createComment(
            User user,
            UUID postId,
            UUID parentId,
            String content
    ) {

        Comment comment = Comment.builder()
                .postId(postId)
                .userId(user.getId())
                .parentId(parentId)
                .content(content)
                .status(CommentStatus.ACTIVE)
                .build();

        return commentRepository.saveAndFlush(comment);
    }
}