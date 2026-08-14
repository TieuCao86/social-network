package com.socialnetwork.module.post.service.impl;

import com.socialnetwork.common.exception.BusinessException;
import com.socialnetwork.common.exception.ErrorCode;
import com.socialnetwork.module.post.dto.request.PostCreateRequest;
import com.socialnetwork.module.post.dto.request.PostMediaRequest;
import com.socialnetwork.module.post.dto.response.PostResponse;
import com.socialnetwork.module.post.entity.Post;
import com.socialnetwork.module.post.entity.PostMedia;
import com.socialnetwork.module.post.entity.PostStatus;
import com.socialnetwork.module.post.entity.PostVisibility;
import com.socialnetwork.module.post.mapper.PostMapper;
import com.socialnetwork.module.post.repository.PostMediaRepository;
import com.socialnetwork.module.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMediaRepository postMediaRepository;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostServiceImpl postService;

    private UUID authorId;
    private UUID postId;
    private Post mockPost;
    private PostResponse mockResponse;

    @BeforeEach
    void setUp() {
        authorId = UUID.randomUUID();
        postId = UUID.randomUUID();

        mockPost = Post.builder()
                .id(postId)
                .authorId(authorId)
                .content("Nội dung bài viết")
                .visibility(PostVisibility.PUBLIC)
                .status(PostStatus.ACTIVE)
                .build();

        mockResponse = PostResponse.builder()
                .id(postId)
                .authorId(authorId)
                .content("Nội dung bài viết")
                .visibility(PostVisibility.PUBLIC)
                .build();
    }

    // =======================================================
    // 1. TESTS CHO CREATE POST
    // =======================================================
    @Nested
    @DisplayName("Tests cho createPost()")
    class CreatePostTests {

        @Test
        @DisplayName("createPost - Thành công với Text và Media")
        void createPost_WithContentAndMedia_Success() {
            PostMediaRequest mediaReq = new PostMediaRequest();
            mediaReq.setFileId(UUID.randomUUID());
            mediaReq.setType(com.socialnetwork.module.post.entity.MediaType.IMAGE);

            PostCreateRequest request = new PostCreateRequest();
            request.setContent("  Bài viết mới  ");
            request.setVisibility(PostVisibility.PUBLIC);
            request.setMediaList(List.of(mediaReq));

            when(postRepository.save(any(Post.class))).thenReturn(mockPost);
            when(postMediaRepository.saveAll(anyList())).thenReturn(Collections.emptyList());
            when(postMapper.toResponse(eq(mockPost), anyList())).thenReturn(mockResponse);

            PostResponse result = postService.createPost(authorId, request);

            assertNotNull(result);
            assertEquals(mockResponse.getId(), result.getId());

            verify(postRepository).save(any(Post.class));
            verify(postMediaRepository).saveAll(anyList());
            verify(postMapper).toResponse(eq(mockPost), anyList());
        }

        @Test
        @DisplayName("createPost - Thành công khi chỉ có Text (không có Media)")
        void createPost_TextOnly_Success() {
            PostCreateRequest request = new PostCreateRequest();
            request.setContent("Chỉ có chữ");
            request.setVisibility(PostVisibility.FRIENDS);

            when(postRepository.save(any(Post.class))).thenReturn(mockPost);
            when(postMapper.toResponse(eq(mockPost), eq(Collections.emptyList()))).thenReturn(mockResponse);

            PostResponse result = postService.createPost(authorId, request);

            assertNotNull(result);
            verify(postRepository).save(any(Post.class));
            verifyNoInteractions(postMediaRepository);
            verify(postMapper).toResponse(eq(mockPost), eq(Collections.emptyList()));
        }

        @Test
        @DisplayName("createPost - Ném exception khi không có cả Text lẫn Media")
        void createPost_EmptyContentAndMedia_ThrowsException() {
            PostCreateRequest request = new PostCreateRequest();
            request.setContent("   "); // Trắng
            request.setMediaList(Collections.emptyList());

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> postService.createPost(authorId, request)
            );

            assertEquals(ErrorCode.INVALID_REQUEST, exception.getErrorCode());
            verifyNoInteractions(postRepository);
            verifyNoInteractions(postMediaRepository);
            verifyNoInteractions(postMapper);
        }
    }

    // =======================================================
    // 2. TESTS CHO GET POST BY ID
    // =======================================================
    @Nested
    @DisplayName("Tests cho getPostById()")
    class GetPostByIdTests {

        @Test
        @DisplayName("getPostById - Thành công khi bài viết tồn tại và ACTIVE")
        void getPostById_Success() {
            List<PostMedia> mediaList = List.of(
                    PostMedia.builder().id(UUID.randomUUID()).postId(postId).sortOrder(0).build()
            );

            when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));
            when(postMediaRepository.findByPostIdOrderBySortOrderAsc(postId)).thenReturn(mediaList);
            when(postMapper.toResponse(mockPost, mediaList)).thenReturn(mockResponse);

            PostResponse result = postService.getPostById(postId);

            assertNotNull(result);
            assertEquals(mockResponse.getId(), result.getId());

            verify(postRepository).findById(postId);
            verify(postMediaRepository).findByPostIdOrderBySortOrderAsc(postId);
            verify(postMapper).toResponse(mockPost, mediaList);
        }

        @Test
        @DisplayName("getPostById - Ném exception khi bài viết không tồn tại")
        void getPostById_NotFound_ThrowsException() {
            when(postRepository.findById(postId)).thenReturn(Optional.empty());

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> postService.getPostById(postId)
            );

            assertEquals(ErrorCode.RESOURCE_NOT_FOUND, exception.getErrorCode());
            verify(postRepository).findById(postId);
            verifyNoInteractions(postMediaRepository);
            verifyNoInteractions(postMapper);
        }

        @Test
        @DisplayName("getPostById - Ném exception khi bài viết có status là DELETED")
        void getPostById_DeletedPost_ThrowsException() {
            mockPost.setStatus(PostStatus.DELETED);
            when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> postService.getPostById(postId)
            );

            assertEquals(ErrorCode.RESOURCE_NOT_FOUND, exception.getErrorCode());
            verify(postRepository).findById(postId);
            verifyNoInteractions(postMediaRepository);
        }
    }

    // =======================================================
    // 3. TESTS CHO GET USER POSTS
    // =======================================================
    @Nested
    @DisplayName("Tests cho getUserPosts()")
    class GetUserPostsTests {

        @Test
        @DisplayName("getUserPosts - Lấy danh sách thành công và gom nhóm media theo post")
        void getUserPosts_Success() {
            Pageable pageable = PageRequest.of(0, 10);
            List<Post> posts = List.of(mockPost);
            Page<Post> postsPage = new PageImpl<>(posts, pageable, 1);

            PostMedia media = PostMedia.builder()
                    .id(UUID.randomUUID())
                    .postId(postId)
                    .sortOrder(0)
                    .build();

            when(postRepository.findByAuthorIdAndStatusOrderByCreatedAtDesc(authorId, PostStatus.ACTIVE, pageable))
                    .thenReturn(postsPage);
            when(postMediaRepository.findByPostIdInOrderByPostIdAscSortOrderAsc(List.of(postId)))
                    .thenReturn(List.of(media));
            when(postMapper.toResponse(eq(mockPost), anyList())).thenReturn(mockResponse);

            Page<PostResponse> result = postService.getUserPosts(authorId, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());

            verify(postRepository).findByAuthorIdAndStatusOrderByCreatedAtDesc(authorId, PostStatus.ACTIVE, pageable);
            verify(postMediaRepository).findByPostIdInOrderByPostIdAscSortOrderAsc(List.of(postId));
            verify(postMapper).toResponse(eq(mockPost), anyList());
        }

        @Test
        @DisplayName("getUserPosts - Trả về empty page khi user không có bài viết")
        void getUserPosts_EmptyPage() {
            Pageable pageable = PageRequest.of(0, 10);
            when(postRepository.findByAuthorIdAndStatusOrderByCreatedAtDesc(authorId, PostStatus.ACTIVE, pageable))
                    .thenReturn(Page.empty(pageable));

            Page<PostResponse> result = postService.getUserPosts(authorId, pageable);

            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(postRepository).findByAuthorIdAndStatusOrderByCreatedAtDesc(authorId, PostStatus.ACTIVE, pageable);
            verifyNoInteractions(postMediaRepository);
            verifyNoInteractions(postMapper);
        }
    }

    // =======================================================
    // 4. TESTS CHO DELETE POST
    // =======================================================
    @Nested
    @DisplayName("Tests cho deletePost()")
    class DeletePostTests {

        @Test
        @DisplayName("deletePost - Chính chủ xóa bài thành công (đổi status sang DELETED)")
        void deletePost_ByAuthor_Success() {
            when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));

            postService.deletePost(postId, authorId);

            assertEquals(PostStatus.DELETED, mockPost.getStatus());
            verify(postRepository).findById(postId);
        }

        @Test
        @DisplayName("deletePost - Người khác xóa bài ném FORBIDDEN exception")
        void deletePost_ByOtherUser_ThrowsForbidden() {
            UUID otherUserId = UUID.randomUUID();
            when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> postService.deletePost(postId, otherUserId)
            );

            assertEquals(ErrorCode.FORBIDDEN, exception.getErrorCode());
            assertNotEquals(PostStatus.DELETED, mockPost.getStatus());
            verify(postRepository).findById(postId);
        }

        @Test
        @DisplayName("deletePost - Bài đã bị xóa trước đó ném RESOURCE_NOT_FOUND")
        void deletePost_AlreadyDeleted_ThrowsException() {
            mockPost.setStatus(PostStatus.DELETED);
            when(postRepository.findById(postId)).thenReturn(Optional.of(mockPost));

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> postService.deletePost(postId, authorId)
            );

            assertEquals(ErrorCode.RESOURCE_NOT_FOUND, exception.getErrorCode());
            verify(postRepository).findById(postId);
        }

        @Test
        @DisplayName("deletePost - Không tìm thấy bài ném RESOURCE_NOT_FOUND")
        void deletePost_NotFound_ThrowsException() {
            when(postRepository.findById(postId)).thenReturn(Optional.empty());

            BusinessException exception = assertThrows(
                    BusinessException.class,
                    () -> postService.deletePost(postId, authorId)
            );

            assertEquals(ErrorCode.RESOURCE_NOT_FOUND, exception.getErrorCode());
            verify(postRepository).findById(postId);
        }
    }
}