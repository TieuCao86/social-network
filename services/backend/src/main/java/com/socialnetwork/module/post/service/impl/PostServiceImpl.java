package com.socialnetwork.module.post.service.impl;

import com.socialnetwork.common.exception.BusinessException;
import com.socialnetwork.common.exception.ErrorCode;
import com.socialnetwork.module.post.dto.request.PostCreateRequest;
import com.socialnetwork.module.post.dto.response.PostResponse;
import com.socialnetwork.module.post.entity.Post;
import com.socialnetwork.module.post.entity.PostMedia;
import com.socialnetwork.module.post.entity.enums.PostStatus;
import com.socialnetwork.module.post.entity.enums.PostVisibility;
import com.socialnetwork.module.post.mapper.PostMapper;
import com.socialnetwork.module.post.repository.PostMediaRepository;
import com.socialnetwork.module.post.repository.PostRepository;
import com.socialnetwork.module.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMediaRepository postMediaRepository;
    private final PostMapper postMapper;

    /**
     * Tạo bài viết mới kèm media (nếu có).
     */
    @Override
    @Transactional
    public PostResponse createPost(UUID authorId, PostCreateRequest request) {
        boolean hasContent = request.getContent() != null && !request.getContent().isBlank();
        boolean hasMedia = request.getMediaList() != null && !request.getMediaList().isEmpty();

        if (!hasContent && !hasMedia) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        // 1. Tạo bài viết
        Post post = Post.builder()
                .authorId(authorId)
                .content(hasContent ? request.getContent().trim() : null)
                .visibility(request.getVisibility())
                .status(PostStatus.ACTIVE)
                .commentCount(0L)
                .reactionCount(0L)
                .build();

        Post savedPost = postRepository.save(post);

        // 2. Lưu danh sách media
        List<PostMedia> savedMediaList = new ArrayList<>();
        if (hasMedia) {
            List<PostMedia> mediaEntities = new ArrayList<>();
            for (int i = 0; i < request.getMediaList().size(); i++) {
                var item = request.getMediaList().get(i);
                PostMedia media = PostMedia.builder()
                        .postId(savedPost.getId())
                        .fileId(item.getFileId())
                        .type(item.getType())
                        .sortOrder(i)
                        .reactionCount(0L)
                        .build();
                mediaEntities.add(media);
            }
            savedMediaList = postMediaRepository.saveAll(mediaEntities);
        }

        return postMapper.toResponse(savedPost, savedMediaList);
    }

    /**
     * Lấy chi tiết một bài viết đang hoạt động.
     */
    @Override
    public PostResponse getPostById(UUID postId) {
        Post post = postRepository.findById(postId)
                .filter(p -> p.getStatus() == PostStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        List<PostMedia> mediaList = postMediaRepository.findByPostIdOrderBySortOrderAsc(postId);

        return postMapper.toResponse(post, mediaList);
    }

    /**
     * Lấy danh sách bài viết của một user (Batch fetch media tránh N+1).
     */
    @Override
    public Page<PostResponse> getUserPosts(UUID authorId, Pageable pageable) {
        Page<Post> postsPage = postRepository.findByAuthorIdAndStatusOrderByCreatedAtDesc(
                authorId,
                PostStatus.ACTIVE,
                pageable
        );

        return mapPostsToResponsePage(postsPage, pageable);
    }

    /**
     * Lấy Public Feed bài viết (Batch fetch media tránh N+1).
     */
    @Override
    public Page<PostResponse> getPublicFeed(Pageable pageable) {
        Page<Post> postsPage = postRepository.findByStatusAndVisibilityOrderByCreatedAtDesc(
                PostStatus.ACTIVE,
                PostVisibility.PUBLIC,
                pageable
        );
        return mapPostsToResponsePage(postsPage, pageable);
    }

    /**
     * Xóa mềm bài viết (Soft delete).
     */
    @Override
    @Transactional
    public void deletePost(UUID postId, UUID currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        // Chỉ tác giả mới có quyền xóa bài viết
        if (!post.getAuthorId().equals(currentUserId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        // Không cho phép xóa lại bài viết đã bị xóa trước đó
        if (post.getStatus() == PostStatus.DELETED) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        post.setStatus(PostStatus.DELETED);
    }

    // ==========================================
    // Helper Methods
    // ==========================================

    /**
     * Gộp chung logic query batch media và map sang Page PostResponse để tránh trùng lặp code.
     */
    private Page<PostResponse> mapPostsToResponsePage(Page<Post> postsPage, Pageable pageable) {
        if (postsPage.isEmpty()) {
            return Page.empty(pageable);
        }

        List<UUID> postIds = postsPage.getContent().stream()
                .map(Post::getId)
                .toList();

        // Query 1 lần lấy toàn bộ media của tất cả các post trên page hiện tại
        Map<UUID, List<PostMedia>> mediaMap = postMediaRepository
                .findByPostIdInOrderByPostIdAscSortOrderAsc(postIds)
                .stream()
                .collect(Collectors.groupingBy(PostMedia::getPostId));

        return postsPage.map(post -> postMapper.toResponse(
                post,
                mediaMap.getOrDefault(post.getId(), Collections.emptyList())
        ));
    }
}