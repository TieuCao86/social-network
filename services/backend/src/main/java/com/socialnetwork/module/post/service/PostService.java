package com.socialnetwork.module.post.service;

import com.socialnetwork.module.post.dto.request.PostCreateRequest;
import com.socialnetwork.module.post.dto.response.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PostService {

    PostResponse createPost(
            UUID authorId,
            PostCreateRequest request
    );

    PostResponse getPostById(
            UUID postId,
            UUID currentUserId
    );

    Page<PostResponse> getUserPosts(
            UUID authorId,
            UUID currentUserId,
            Pageable pageable
    );

    Page<PostResponse> getFeed(
            UUID currentUserId,
            Pageable pageable
    );

    void deletePost(
            UUID postId,
            UUID currentUserId
    );
}