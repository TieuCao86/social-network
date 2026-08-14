package com.socialnetwork.module.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialnetwork.common.security.CustomUserDetails;
import com.socialnetwork.module.post.dto.request.PostCreateRequest;
import com.socialnetwork.module.post.dto.request.PostMediaRequest;
import com.socialnetwork.module.post.entity.Post;
import com.socialnetwork.module.post.entity.PostMedia;
import com.socialnetwork.module.post.entity.PostStatus;
import com.socialnetwork.module.post.entity.PostVisibility;
import com.socialnetwork.module.post.repository.PostMediaRepository;
import com.socialnetwork.module.post.repository.PostRepository;
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

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional // Tự động Rollback DB sau mỗi testcase
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostMediaRepository postMediaRepository;

    private User author;
    private User otherUser;
    private CustomUserDetails authorDetails;
    private CustomUserDetails otherUserDetails;

    @BeforeEach
    void setUp() {
        postMediaRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();

        // 1. Tạo Tác giả bài viết
        author = User.builder()
                .username("post_author")
                .email("author@example.com")
                .passwordHash("hashed_password")
                .status(UserStatus.ACTIVE)
                .role(UserRole.USER)
                .build();
        author = userRepository.saveAndFlush(author);
        authorDetails = new CustomUserDetails(author);

        // 2. Tạo User khác (dùng cho testcase check quyền xóa)
        otherUser = User.builder()
                .username("other_user")
                .email("other@example.com")
                .passwordHash("hashed_password")
                .status(UserStatus.ACTIVE)
                .role(UserRole.USER)
                .build();
        otherUser = userRepository.saveAndFlush(otherUser);
        otherUserDetails = new CustomUserDetails(otherUser);
    }

    // =======================================================
    // 1. TEST POST /api/posts (TẠO BÀI VIẾT)
    // =======================================================

    @Test
    @DisplayName("POST /api/posts - Tạo bài viết thành công (Text + Media)")
    void createPost_Success() throws Exception {
        PostMediaRequest mediaRequest = new PostMediaRequest();
        mediaRequest.setFileId(UUID.randomUUID());
        mediaRequest.setType(com.socialnetwork.module.post.entity.MediaType.IMAGE);

        PostCreateRequest request = new PostCreateRequest();
        request.setContent("Xin chào mạng xã hội!");
        request.setVisibility(PostVisibility.PUBLIC);
        request.setMediaList(List.of(mediaRequest));

        mockMvc.perform(post("/api/posts")
                        .with(user(authorDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").value("Xin chào mạng xã hội!"))
                .andExpect(jsonPath("$.data.mediaList", hasSize(1)))
                .andExpect(jsonPath("$.data.mediaList[0].fileId").value(mediaRequest.getFileId().toString()));
    }

    @Test
    @DisplayName("POST /api/posts - Bắt lỗi khi bài viết trống cả Content lẫn Media")
    void createPost_EmptyContentAndMedia_ShouldReturnBadRequest() throws Exception {
        PostCreateRequest request = new PostCreateRequest();

        mockMvc.perform(post("/api/posts")
                        .with(user(authorDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/posts - Trả về 401 Unauthorized khi chưa đăng nhập")
    void createPost_Unauthenticated_ShouldReturn401() throws Exception {
        PostCreateRequest request = new PostCreateRequest();
        request.setContent("Bài viết chưa login");

        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    // =======================================================
    // 2. TEST GET /api/posts/{id} (LẤY CHI TIẾT)
    // =======================================================

    @Test
    @DisplayName("GET /api/posts/{id} - Lấy chi tiết bài viết thành công")
    void getPostById_Success() throws Exception {
        Post post = postRepository.saveAndFlush(Post.builder()
                .authorId(author.getId())
                .content("Nội dung bài viết mẫu")
                .visibility(PostVisibility.PUBLIC)
                .status(PostStatus.ACTIVE)
                .build());

        mockMvc.perform(get("/api/posts/{id}", post.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(post.getId().toString()))
                .andExpect(jsonPath("$.data.content").value("Nội dung bài viết mẫu"));
    }

    @Test
    @DisplayName("GET /api/posts/{id} - Trả về 404 Not Found khi post không tồn tại")
    void getPostById_NotFound_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/posts/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    // =======================================================
    // 3. TEST GET /api/posts/user/{authorId} (LẤY THEO USER)
    // =======================================================

    @Test
    @DisplayName("GET /api/posts/user/{authorId} - Lấy danh sách bài viết phân trang")
    void getUserPosts_Success() throws Exception {
        postRepository.saveAndFlush(Post.builder()
                .authorId(author.getId())
                .content("Bài 1")
                .status(PostStatus.ACTIVE)
                .build());

        postRepository.saveAndFlush(Post.builder()
                .authorId(author.getId())
                .content("Bài 2")
                .status(PostStatus.ACTIVE)
                .build());

        mockMvc.perform(get("/api/posts/user/{authorId}", author.getId())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(2)))
                .andExpect(jsonPath("$.data.totalElements").value(2));
    }

    // =======================================================
    // 4. TEST DELETE /api/posts/{id} (XÓA BÀI VIẾT)
    // =======================================================

    @Test
    @DisplayName("DELETE /api/posts/{id} - Chính chủ xóa bài viết thành công (Soft Delete)")
    void deletePost_ByAuthor_Success() throws Exception {
        Post post = postRepository.saveAndFlush(Post.builder()
                .authorId(author.getId())
                .content("Bài viết sắp xóa")
                .status(PostStatus.ACTIVE)
                .build());

        mockMvc.perform(delete("/api/posts/{id}", post.getId())
                        .with(user(authorDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Xóa bài viết thành công"));

        Post deletedPost = postRepository.findById(post.getId()).orElseThrow();
        assertEquals(PostStatus.DELETED, deletedPost.getStatus());
    }

    @Test
    @DisplayName("DELETE /api/posts/{id} - Trả về 403 Forbidden khi người khác cố tình xóa bài")
    void deletePost_ByOtherUser_ShouldReturn403Forbidden() throws Exception {
        Post post = postRepository.saveAndFlush(Post.builder()
                .authorId(author.getId())
                .content("Bài viết của author")
                .status(PostStatus.ACTIVE)
                .build());

        mockMvc.perform(delete("/api/posts/{id}", post.getId())
                        .with(user(otherUserDetails)))
                .andExpect(status().isForbidden());
    }
}