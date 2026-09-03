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
import com.socialnetwork.module.relationship.entity.enums.FriendshipStatus;
import com.socialnetwork.module.relationship.repository.FriendshipRepository;
import com.socialnetwork.module.user.entity.User;
import com.socialnetwork.module.user.repository.UserRepository;
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
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    private final PostMapper postMapper;

    /**
     * Tạo bài viết mới kèm media.
     */
    @Override
    @Transactional
    public PostResponse createPost(
            UUID authorId,
            PostCreateRequest request
    ) {
        boolean hasContent =
                request.getContent() != null
                        && !request.getContent().isBlank();

        boolean hasMedia =
                request.getMediaList() != null
                        && !request.getMediaList().isEmpty();

        if (!hasContent && !hasMedia) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }

        Post post = Post.builder()
                .authorId(authorId)
                .content(hasContent
                        ? request.getContent().trim()
                        : null)
                .visibility(request.getVisibility())
                .status(PostStatus.ACTIVE)
                .commentCount(0L)
                .reactionCount(0L)
                .build();

        Post savedPost = postRepository.save(post);

        List<PostMedia> savedMediaList = Collections.emptyList();

        if (hasMedia) {
            List<PostMedia> mediaEntities =
                    IntStream.range(0, request.getMediaList().size())
                            .mapToObj(i -> {
                                var item = request.getMediaList().get(i);

                                return PostMedia.builder()
                                        .postId(savedPost.getId())
                                        .fileId(item.getFileId())
                                        .type(item.getType())
                                        .sortOrder(i)
                                        .reactionCount(0L)
                                        .build();
                            })
                            .collect(Collectors.toList());

            savedMediaList = postMediaRepository.saveAll(mediaEntities);
        }

        return postMapper.toResponse(
                savedPost,
                savedMediaList
        );
    }

    /**
     * Lấy chi tiết bài viết.
     */
    @Override
    public PostResponse getPostById(UUID postId) {

        Post post = postRepository.findById(postId)
                .filter(p -> p.getStatus() == PostStatus.ACTIVE)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.RESOURCE_NOT_FOUND
                        )
                );

        List<PostMedia> mediaList =
                postMediaRepository
                        .findByPostIdOrderBySortOrderAsc(postId);

        return postMapper.toResponse(
                post,
                mediaList
        );
    }

    /**
     * Lấy bài viết của user.
     */
    @Override
    public Page<PostResponse> getUserPosts(
            UUID authorId,
            Pageable pageable
    ) {
        Page<Post> postsPage =
                postRepository
                        .findByAuthorIdAndStatusOrderByCreatedAtDesc(
                                authorId,
                                PostStatus.ACTIVE,
                                pageable
                        );

        return buildResponsePage(postsPage);
    }

    /**
     * Lấy bảng tin của user gồm bài viết của chính mình và bạn bè.
     */
    @Override
    public Page<PostResponse> getFeed(
            UUID currentUserId,
            Pageable pageable
    ) {
        List<UUID> friendIds =
                friendshipRepository.findFriendIdsByUserIdAndStatus(
                        currentUserId,
                        FriendshipStatus.ACCEPTED
                );

        List<UUID> authorIds = new ArrayList<>(friendIds);

        if (!authorIds.contains(currentUserId)) {
            authorIds.add(currentUserId);
        }

        Page<Post> postsPage =
                postRepository.findByAuthorIdInAndStatusOrderByCreatedAtDesc(
                        authorIds,
                        PostStatus.ACTIVE,
                        pageable
                );

        return buildResponsePage(postsPage);
    }

    /**
     * Xóa mềm bài viết.
     */
    @Override
    @Transactional
    public void deletePost(
            UUID postId,
            UUID currentUserId
    ) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.RESOURCE_NOT_FOUND
                        )
                );

        if (!post.getAuthorId().equals(currentUserId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        if (post.getStatus() == PostStatus.DELETED) {
            throw new BusinessException(
                    ErrorCode.RESOURCE_NOT_FOUND
            );
        }

        post.setStatus(PostStatus.DELETED);
    }

    /**
     * Batch lấy media và chuyển Page<Post> thành Page<PostResponse>.
     */
    private Page<PostResponse> buildResponsePage(
            Page<Post> postsPage
    ) {
        if (postsPage.isEmpty()) {
            return Page.empty(postsPage.getPageable());
        }

        List<UUID> postIds = postsPage.getContent()
                .stream()
                .map(Post::getId)
                .toList();

        List<PostMedia> mediaList =
                postMediaRepository
                        .findByPostIdInOrderByPostIdAscSortOrderAsc(postIds);

        Map<UUID, List<PostMedia>> mediaMap =
                mediaList.stream()
                        .collect(Collectors.groupingBy(
                                PostMedia::getPostId
                        ));

        List<UUID> authorIds = postsPage.getContent()
                .stream()
                .map(Post::getAuthorId)
                .distinct()
                .toList();

        Map<UUID, User> userMap =
                userRepository.findAllById(authorIds)
                        .stream()
                        .collect(Collectors.toMap(
                                User::getId,
                                user -> user
                        ));

        return postMapper.toResponsePage(
                postsPage,
                mediaMap,
                userMap
        );
    }
}