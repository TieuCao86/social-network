package com.socialnetwork.module.post.service.impl;

import com.socialnetwork.common.exception.BusinessException;
import com.socialnetwork.common.exception.ErrorCode;
import com.socialnetwork.module.post.dto.request.PostCreateRequest;
import com.socialnetwork.module.post.dto.request.PostMediaRequest;
import com.socialnetwork.module.post.dto.response.PostAuthorResponse;
import com.socialnetwork.module.post.dto.response.PostResponse;
import com.socialnetwork.module.post.entity.Post;
import com.socialnetwork.module.post.entity.PostMedia;
import com.socialnetwork.module.post.entity.enums.MediaType;
import com.socialnetwork.module.post.entity.enums.PostStatus;
import com.socialnetwork.module.post.entity.enums.PostVisibility;
import com.socialnetwork.module.post.mapper.PostMapper;
import com.socialnetwork.module.post.repository.PostMediaRepository;
import com.socialnetwork.module.post.repository.PostReactionRepository;
import com.socialnetwork.module.post.repository.PostRepository;
import com.socialnetwork.module.relationship.entity.enums.FriendshipStatus;
import com.socialnetwork.module.relationship.repository.FriendshipRepository;
import com.socialnetwork.module.user.entity.User;
import com.socialnetwork.module.user.repository.UserRepository;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMediaRepository postMediaRepository;

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostReactionRepository postReactionRepository;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostServiceImpl postService;

    private UUID authorId;
    private UUID postId;

    private Post mockPost;
    private PostResponse mockResponse;
    private User mockUser;

    @BeforeEach
    void setUp() {
        authorId = UUID.randomUUID();
        postId = UUID.randomUUID();

        // KHỞI TẠO mockPost TẠI ĐÂY
        mockPost = Post.builder()
                .id(postId)
                .authorId(authorId)
                .content("Nội dung bài viết")
                .visibility(PostVisibility.PUBLIC)
                .status(PostStatus.ACTIVE)
                .reactionCount(0L)
                .commentCount(0L)
                .build();

        PostAuthorResponse mockAuthor =
                PostAuthorResponse.builder()
                        .id(authorId)
                        .username("testuser")
                        .fullName("Test User")
                        .build();

        mockResponse = PostResponse.builder()
                .id(postId)
                .author(mockAuthor)
                .content("Nội dung bài viết")
                .visibility(PostVisibility.PUBLIC)
                .build();

        mockUser = User.builder()
                .id(authorId)
                .username("testuser")
                .email("test@example.com")
                .build();
    }

    // =======================================================
    // CREATE POST
    // =======================================================

    @Nested
    @DisplayName("Tests cho createPost()")
    class CreatePostTests {

        @Test
        @DisplayName("createPost - Thành công với Text và Media")
        void createPost_WithContentAndMedia_Success() {

            PostMediaRequest mediaRequest =
                    new PostMediaRequest();

            mediaRequest.setFileId(UUID.randomUUID());
            mediaRequest.setType(MediaType.IMAGE);

            PostCreateRequest request =
                    new PostCreateRequest();

            request.setContent("  Bài viết mới  ");
            request.setVisibility(PostVisibility.PUBLIC);
            request.setMediaList(List.of(mediaRequest));

            Post savedPost = Post.builder()
                    .id(postId)
                    .authorId(authorId)
                    .content("Bài viết mới")
                    .visibility(PostVisibility.PUBLIC)
                    .status(PostStatus.ACTIVE)
                    .reactionCount(0L)
                    .commentCount(0L)
                    .build();

            when(postRepository.save(any(Post.class)))
                    .thenReturn(savedPost);

            when(postMediaRepository.saveAll(anyList()))
                    .thenReturn(Collections.emptyList());

            when(postMapper.toResponse(
                    eq(savedPost),
                    anyList()
            )).thenReturn(mockResponse);

            PostResponse result =
                    postService.createPost(authorId, request);

            assertNotNull(result);
            assertEquals(postId, result.getId());

            verify(postRepository).save(argThat(post ->
                    post.getAuthorId().equals(authorId)
                            && post.getContent().equals("Bài viết mới")
                            && post.getVisibility()
                            == PostVisibility.PUBLIC
                            && post.getStatus()
                            == PostStatus.ACTIVE
            ));

            verify(postMediaRepository)
                    .saveAll(anyList());

            verify(postMapper)
                    .toResponse(eq(savedPost), anyList());
        }

        @Test
        @DisplayName("createPost - Thành công khi chỉ có Text")
        void createPost_TextOnly_Success() {

            PostCreateRequest request =
                    new PostCreateRequest();

            request.setContent("Chỉ có chữ");
            request.setVisibility(PostVisibility.FRIENDS);

            when(postRepository.save(any(Post.class)))
                    .thenReturn(mockPost);

            when(postMapper.toResponse(
                    eq(mockPost),
                    eq(Collections.emptyList())
            )).thenReturn(mockResponse);

            PostResponse result =
                    postService.createPost(authorId, request);

            assertNotNull(result);

            verify(postRepository).save(any(Post.class));

            verify(postMediaRepository, never())
                    .saveAll(anyList());

            verify(postMapper)
                    .toResponse(
                            eq(mockPost),
                            eq(Collections.emptyList())
                    );
        }

        @Test
        @DisplayName("createPost - Thành công khi chỉ có Media")
        void createPost_MediaOnly_Success() {

            PostMediaRequest mediaRequest =
                    new PostMediaRequest();

            mediaRequest.setFileId(UUID.randomUUID());
            mediaRequest.setType(MediaType.IMAGE);

            PostCreateRequest request =
                    new PostCreateRequest();

            request.setContent(null);
            request.setVisibility(PostVisibility.PUBLIC);
            request.setMediaList(List.of(mediaRequest));

            when(postRepository.save(any(Post.class)))
                    .thenReturn(mockPost);

            when(postMediaRepository.saveAll(anyList()))
                    .thenReturn(Collections.emptyList());

            when(postMapper.toResponse(
                    eq(mockPost),
                    anyList()
            )).thenReturn(mockResponse);

            PostResponse result =
                    postService.createPost(authorId, request);

            assertNotNull(result);

            verify(postRepository).save(any(Post.class));
            verify(postMediaRepository).saveAll(anyList());
        }

        @Test
        @DisplayName("createPost - Ném exception khi không có Text và Media")
        void createPost_EmptyContentAndMedia_ThrowsException() {

            PostCreateRequest request =
                    new PostCreateRequest();

            request.setContent("   ");
            request.setMediaList(Collections.emptyList());

            BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> postService.createPost(
                                    authorId,
                                    request
                            )
                    );

            assertEquals(
                    ErrorCode.INVALID_REQUEST,
                    exception.getErrorCode()
            );

            verifyNoInteractions(
                    postRepository,
                    postMediaRepository,
                    postMapper
            );
        }
    }


    // =======================================================
    // GET POST BY ID
    // =======================================================

    @Nested
    @DisplayName("Tests cho getPostById()")
    class GetPostByIdTests {

        @Test
        @DisplayName("getPostById - Thành công")
        void getPostById_Success() {

            List<PostMedia> mediaList =
                    List.of(
                            PostMedia.builder()
                                    .id(UUID.randomUUID())
                                    .postId(postId)
                                    .sortOrder(0)
                                    .build()
                    );

            when(postRepository.findById(postId))
                    .thenReturn(Optional.of(mockPost));

            when(postMediaRepository
                    .findByPostIdOrderBySortOrderAsc(postId))
                    .thenReturn(mediaList);

            when(postMapper.toResponse(
                    mockPost,
                    mediaList
            )).thenReturn(mockResponse);

            PostResponse result =
                    postService.getPostById(
                            postId,
                            authorId
                    );

            assertNotNull(result);
            assertEquals(postId, result.getId());

            verify(postRepository).findById(postId);

            verify(postMediaRepository)
                    .findByPostIdOrderBySortOrderAsc(postId);

            verify(postMapper)
                    .toResponse(mockPost, mediaList);
        }

        @Test
        @DisplayName("getPostById - Không tìm thấy bài viết")
        void getPostById_NotFound_ThrowsException() {

            when(postRepository.findById(postId))
                    .thenReturn(Optional.empty());

            BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> postService.getPostById(
                                    postId,
                                    authorId
                            )
                    );

            assertEquals(
                    ErrorCode.RESOURCE_NOT_FOUND,
                    exception.getErrorCode()
            );

            verify(postRepository).findById(postId);

            verifyNoInteractions(
                    postMediaRepository,
                    postMapper
            );
        }

        @Test
        @DisplayName("getPostById - Bài viết đã bị xóa")
        void getPostById_DeletedPost_ThrowsException() {

            mockPost.setStatus(PostStatus.DELETED);

            when(postRepository.findById(postId))
                    .thenReturn(Optional.of(mockPost));

            BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> postService.getPostById(
                                    postId,
                                    authorId
                            )
                    );

            assertEquals(
                    ErrorCode.RESOURCE_NOT_FOUND,
                    exception.getErrorCode()
            );

            verify(postRepository).findById(postId);

            verifyNoInteractions(
                    postMediaRepository,
                    postMapper
            );
        }
    }


    // =======================================================
    // GET USER POSTS
    // =======================================================

    @Nested
    @DisplayName("Tests cho getUserPosts()")
    class GetUserPostsTests {

        @Test
        @DisplayName("getUserPosts - Thành công")
        void getUserPosts_Success() {

            Pageable pageable =
                    PageRequest.of(0, 10);

            Page<Post> postsPage =
                    new PageImpl<>(
                            List.of(mockPost),
                            pageable,
                            1
                    );

            PostMedia media =
                    PostMedia.builder()
                            .id(UUID.randomUUID())
                            .postId(postId)
                            .sortOrder(0)
                            .build();

            Page<PostResponse> responsePage =
                    new PageImpl<>(
                            List.of(mockResponse),
                            pageable,
                            1
                    );

            when(postRepository
                    .findByAuthorIdAndStatusOrderByCreatedAtDesc(
                            authorId,
                            PostStatus.ACTIVE,
                            pageable
                    ))
                    .thenReturn(postsPage);

            when(postMediaRepository
                    .findByPostIdInOrderByPostIdAscSortOrderAsc(
                            List.of(postId)
                    ))
                    .thenReturn(List.of(media));

            when(userRepository.findAllById(List.of(authorId)))
                    .thenReturn(List.of(mockUser));

            when(postMapper.toResponse(eq(mockPost), anyList()))
                    .thenReturn(mockResponse);

            when(postMapper.toAuthorResponse(any(User.class)))
                    .thenReturn(mockResponse.getAuthor());

            Page<PostResponse> result =
                    postService.getUserPosts(
                            authorId,
                            authorId,
                            pageable
                    );

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());

            verify(postRepository)
                    .findByAuthorIdAndStatusOrderByCreatedAtDesc(
                            authorId,
                            PostStatus.ACTIVE,
                            pageable
                    );

            verify(postMediaRepository)
                    .findByPostIdInOrderByPostIdAscSortOrderAsc(
                            List.of(postId)
                    );

            verify(userRepository)
                    .findAllById(List.of(authorId));

            verify(postMapper).toResponse(eq(mockPost), anyList());
        }

        @Test
        @DisplayName("getUserPosts - Trả về empty page")
        void getUserPosts_EmptyPage() {

            Pageable pageable =
                    PageRequest.of(0, 10);

            when(postRepository
                    .findByAuthorIdAndStatusOrderByCreatedAtDesc(
                            authorId,
                            PostStatus.ACTIVE,
                            pageable
                    ))
                    .thenReturn(Page.empty(pageable));

            Page<PostResponse> result =
                    postService.getUserPosts(
                            authorId,
                            authorId,
                            pageable
                    );

            assertNotNull(result);
            assertTrue(result.isEmpty());

            verify(postRepository)
                    .findByAuthorIdAndStatusOrderByCreatedAtDesc(
                            authorId,
                            PostStatus.ACTIVE,
                            pageable
                    );

            verifyNoInteractions(
                    postMediaRepository,
                    userRepository,
                    postMapper
            );
        }
    }


    // =======================================================
    // GET FEED
    // =======================================================

    @Nested
    @DisplayName("Tests cho getFeed()")
    class GetFeedTests {

        @Test
        @DisplayName("getFeed - Lấy bài viết của mình và bạn bè thành công")
        void getFeed_Success() {
            // 1. Chuẩn bị IDs và Entity
            UUID friendId = UUID.randomUUID();
            UUID friendPostId = UUID.randomUUID();

            Post friendPost = Post.builder()
                    .id(friendPostId)
                    .authorId(friendId)
                    .content("Bài viết của bạn")
                    .visibility(PostVisibility.PUBLIC)
                    .status(PostStatus.ACTIVE)
                    .reactionCount(0L)
                    .commentCount(0L)
                    .build();

            User friendUser = User.builder()
                    .id(friendId)
                    .username("friend")
                    .email("friend@example.com")
                    .build();

            // 2. Chuẩn bị DTO Response
            PostAuthorResponse friendAuthor = PostAuthorResponse.builder()
                    .id(friendId)
                    .username("friend")
                    .fullName("Friend")
                    .build();

            PostResponse friendResponse = PostResponse.builder()
                    .id(friendPostId)
                    .author(friendAuthor)
                    .content("Bài viết của bạn")
                    .build();

            Pageable pageable = PageRequest.of(0, 10);
            Page<Post> postsPage = new PageImpl<>(List.of(mockPost, friendPost), pageable, 2);

            // 3. Stub các repository
            when(friendshipRepository.findFriendIdsByUserIdAndStatus(
                    authorId,
                    FriendshipStatus.ACCEPTED
            )).thenReturn(List.of(friendId));

            when(postRepository.findByAuthorIdInAndStatusOrderByCreatedAtDesc(
                    anyList(),
                    eq(PostStatus.ACTIVE),
                    eq(pageable)
            )).thenReturn(postsPage);

            when(postMediaRepository.findByPostIdInOrderByPostIdAscSortOrderAsc(anyList()))
                    .thenReturn(Collections.emptyList());

            when(userRepository.findAllById(anyList()))
                    .thenReturn(List.of(mockUser, friendUser));

            when(postReactionRepository.findByUserIdAndPostIdIn(any(UUID.class), anyList()))
                    .thenReturn(Collections.emptyList());

            // 4. Stub Mapper (toResponse & toAuthorResponse)
            when(postMapper.toResponse(eq(mockPost), anyList()))
                    .thenReturn(mockResponse);
            when(postMapper.toResponse(eq(friendPost), anyList()))
                    .thenReturn(friendResponse);

            when(postMapper.toAuthorResponse(eq(mockUser)))
                    .thenReturn(mockResponse.getAuthor());
            when(postMapper.toAuthorResponse(eq(friendUser)))
                    .thenReturn(friendAuthor);

            // 5. Thực thi method
            Page<PostResponse> result = postService.getFeed(authorId, pageable);

            // 6. Assertions
            assertNotNull(result);
            assertEquals(2, result.getTotalElements());

            // 7. Verifications
            verify(friendshipRepository).findFriendIdsByUserIdAndStatus(
                    authorId,
                    FriendshipStatus.ACCEPTED
            );

            verify(postRepository).findByAuthorIdInAndStatusOrderByCreatedAtDesc(
                    argThat(ids -> ids.contains(authorId) && ids.contains(friendId)),
                    eq(PostStatus.ACTIVE),
                    eq(pageable)
            );

            verify(postMediaRepository).findByPostIdInOrderByPostIdAscSortOrderAsc(
                    argThat(ids -> ids.contains(postId) && ids.contains(friendPostId))
            );

            verify(userRepository).findAllById(
                    argThat((Iterable<UUID> ids) -> {
                        Collection<UUID> idList = (Collection<UUID>) ids;
                        return idList.contains(authorId) && idList.contains(friendId);
                    })
            );

            verify(postMapper, times(2)).toResponse(any(Post.class), anyList());
        }

        @Test
        @DisplayName("getFeed - Không có bạn bè vẫn lấy bài viết của chính mình")
        void getFeed_NoFriends_StillIncludeCurrentUser() {

            Pageable pageable = PageRequest.of(0, 10);

            Page<Post> postsPage =
                    new PageImpl<>(
                            List.of(mockPost),
                            pageable,
                            1
                    );

            // 1. Stub friendship
            when(friendshipRepository
                    .findFriendIdsByUserIdAndStatus(
                            authorId,
                            FriendshipStatus.ACCEPTED
                    ))
                    .thenReturn(Collections.emptyList());

            // 2. Stub post repository
            when(postRepository
                    .findByAuthorIdInAndStatusOrderByCreatedAtDesc(
                            anyList(),
                            eq(PostStatus.ACTIVE),
                            eq(pageable)
                    ))
                    .thenReturn(postsPage);

            // 3. Stub post media repository
            when(postMediaRepository
                    .findByPostIdInOrderByPostIdAscSortOrderAsc(
                            List.of(postId)
                    ))
                    .thenReturn(Collections.emptyList());

            // 4. Stub user repository
            when(userRepository.findAllById(List.of(authorId)))
                    .thenReturn(List.of(mockUser));

            // 5. Stub reaction repository
            when(postReactionRepository
                    .findByUserIdAndPostIdIn(any(UUID.class), anyList()))
                    .thenReturn(Collections.emptyList());

            // 6. Stub mapper (THAY THẾ toResponsePage BẰNG toResponse & toAuthorResponse)
            when(postMapper.toResponse(eq(mockPost), anyList()))
                    .thenReturn(mockResponse);

            when(postMapper.toAuthorResponse(eq(mockUser)))
                    .thenReturn(mockResponse.getAuthor());

            // Execute
            Page<PostResponse> result =
                    postService.getFeed(
                            authorId,
                            pageable
                    );

            // Assertions
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());

            // Verifications
            verify(postRepository)
                    .findByAuthorIdInAndStatusOrderByCreatedAtDesc(
                            argThat(ids ->
                                    ids.size() == 1
                                            && ids.contains(authorId)
                            ),
                            eq(PostStatus.ACTIVE),
                            eq(pageable)
                    );

            verify(postMapper).toResponse(eq(mockPost), anyList());
        }

        @Test
        @DisplayName("getFeed - Empty feed")
        void getFeed_EmptyFeed() {

            Pageable pageable =
                    PageRequest.of(0, 10);

            when(friendshipRepository
                    .findFriendIdsByUserIdAndStatus(
                            authorId,
                            FriendshipStatus.ACCEPTED
                    ))
                    .thenReturn(Collections.emptyList());

            when(postRepository
                    .findByAuthorIdInAndStatusOrderByCreatedAtDesc(
                            anyList(),
                            eq(PostStatus.ACTIVE),
                            eq(pageable)
                    ))
                    .thenReturn(Page.empty(pageable));

            Page<PostResponse> result =
                    postService.getFeed(
                            authorId,
                            pageable
                    );

            assertNotNull(result);
            assertTrue(result.isEmpty());

            verifyNoInteractions(
                    postMediaRepository,
                    userRepository,
                    postMapper
            );
        }
    }


    // =======================================================
    // DELETE POST
    // =======================================================

    @Nested
    @DisplayName("Tests cho deletePost()")
    class DeletePostTests {

        @Test
        @DisplayName("deletePost - Chính chủ xóa thành công")
        void deletePost_ByAuthor_Success() {

            when(postRepository.findById(postId))
                    .thenReturn(Optional.of(mockPost));

            postService.deletePost(
                    postId,
                    authorId
            );

            assertEquals(
                    PostStatus.DELETED,
                    mockPost.getStatus()
            );

            verify(postRepository).findById(postId);
        }

        @Test
        @DisplayName("deletePost - Người khác xóa ném FORBIDDEN")
        void deletePost_ByOtherUser_ThrowsForbidden() {

            UUID otherUserId =
                    UUID.randomUUID();

            when(postRepository.findById(postId))
                    .thenReturn(Optional.of(mockPost));

            BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> postService.deletePost(
                                    postId,
                                    otherUserId
                            )
                    );

            assertEquals(
                    ErrorCode.FORBIDDEN,
                    exception.getErrorCode()
            );

            assertNotEquals(
                    PostStatus.DELETED,
                    mockPost.getStatus()
            );
        }

        @Test
        @DisplayName("deletePost - Bài đã bị xóa")
        void deletePost_AlreadyDeleted_ThrowsException() {

            mockPost.setStatus(PostStatus.DELETED);

            when(postRepository.findById(postId))
                    .thenReturn(Optional.of(mockPost));

            BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> postService.deletePost(
                                    postId,
                                    authorId
                            )
                    );

            assertEquals(
                    ErrorCode.RESOURCE_NOT_FOUND,
                    exception.getErrorCode()
            );
        }

        @Test
        @DisplayName("deletePost - Không tìm thấy bài")
        void deletePost_NotFound_ThrowsException() {

            when(postRepository.findById(postId))
                    .thenReturn(Optional.empty());

            BusinessException exception =
                    assertThrows(
                            BusinessException.class,
                            () -> postService.deletePost(
                                    postId,
                                    authorId
                            )
                    );

            assertEquals(
                    ErrorCode.RESOURCE_NOT_FOUND,
                    exception.getErrorCode()
            );

            verify(postRepository).findById(postId);
        }
    }
}
