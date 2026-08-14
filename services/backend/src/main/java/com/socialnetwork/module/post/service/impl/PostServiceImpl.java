package com.socialnetwork.module.post.service.impl;

import com.socialnetwork.common.exception.BusinessException;
import com.socialnetwork.common.exception.ErrorCode;
import com.socialnetwork.module.post.dto.request.PostCreateRequest;
import com.socialnetwork.module.post.dto.response.PostResponse;
import com.socialnetwork.module.post.entity.Post;
import com.socialnetwork.module.post.entity.PostMedia;
import com.socialnetwork.module.post.entity.PostStatus;
import com.socialnetwork.module.post.mapper.PostMapper;
import com.socialnetwork.module.post.repository.PostMediaRepository;
import com.socialnetwork.module.post.repository.PostRepository;
import com.socialnetwork.module.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMediaRepository postMediaRepository;
    private final PostMapper postMapper;

    @Override
    @Transactional
    public PostResponse createPost(UUID authorId, PostCreateRequest request) {
        boolean hasContent = request.getContent() != null && !request.getContent().isBlank();
        boolean hasMedia = request.getMediaList() != null && !request.getMediaList().isEmpty();

        // Bài viết phải có ít nhất Content hoặc Media
        if (!hasContent && !hasMedia) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        // 1. Tạo Post
        Post post = Post.builder()
                .authorId(authorId)
                .content(hasContent ? request.getContent().trim() : null)
                .visibility(request.getVisibility())
                .status(PostStatus.ACTIVE)
                .build();

        post = postRepository.save(post);

        // 2. Tạo danh sách Media liên kết
        List<PostMedia> savedMediaList = new ArrayList<>();
        if (hasMedia) {
            UUID postId = post.getId();
            List<PostMedia> mediaEntities = new ArrayList<>();

            for (int i = 0; i < request.getMediaList().size(); i++) {
                var item = request.getMediaList().get(i);
                PostMedia media = PostMedia.builder()
                        .postId(postId)
                        .fileId(item.getFileId())
                        .type(item.getType())
                        .sortOrder(i)
                        .build();

                mediaEntities.add(media);
            }

            savedMediaList = postMediaRepository.saveAll(mediaEntities);
        }

        return postMapper.toResponse(post, savedMediaList);
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponse getPostById(UUID postId) {
        Post post = postRepository.findById(postId)
                .filter(p -> p.getStatus() == PostStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        List<PostMedia> mediaList = postMediaRepository.findByPostIdOrderBySortOrderAsc(postId);

        return postMapper.toResponse(post, mediaList);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PostResponse> getUserPosts(UUID authorId, Pageable pageable) {
        Page<Post> postsPage = postRepository.findByAuthorIdAndStatusOrderByCreatedAtDesc(
                authorId,
                PostStatus.ACTIVE,
                pageable
        );

        if (postsPage.isEmpty()) {
            return Page.empty(pageable);
        }

        List<UUID> postIds = postsPage.getContent().stream()
                .map(Post::getId)
                .toList();

        // Lấy danh sách media của tất cả bài viết trong 1 query duy nhất (Tránh N+1 Issue)
        List<PostMedia> allMedia = postMediaRepository.findByPostIdInOrderByPostIdAscSortOrderAsc(postIds);

        // Group media theo postId
        Map<UUID, List<PostMedia>> mediaMap = allMedia.stream()
                .collect(Collectors.groupingBy(PostMedia::getPostId));

        return postsPage.map(post -> postMapper.toResponse(
                post,
                mediaMap.getOrDefault(post.getId(), Collections.emptyList())
        ));
    }

    @Override
    @Transactional
    public void deletePost(UUID postId, UUID currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        // Chỉ tác giả bài viết mới có quyền xóa
        if (!post.getAuthorId().equals(currentUserId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        // Đã xóa trước đó rồi
        if (post.getStatus() == PostStatus.DELETED) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        // Soft delete bài viết
        post.setStatus(PostStatus.DELETED);
    }
}