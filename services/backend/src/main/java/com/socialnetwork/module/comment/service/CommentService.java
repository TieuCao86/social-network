package com.socialnetwork.module.comment.service;

import com.socialnetwork.module.comment.dto.request.CreateCommentRequest;
import com.socialnetwork.module.comment.dto.request.UpdateCommentRequest;
import com.socialnetwork.module.comment.dto.response.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CommentService {

    CommentResponse createComment(
            UUID currentUserId,
            UUID postId,
            CreateCommentRequest request
    );

    CommentResponse updateComment(
            UUID currentUserId,
            UUID commentId,
            UpdateCommentRequest request
    );

    void deleteComment(
            UUID currentUserId,
            UUID commentId
    );

    Page<CommentResponse> getComments(
            UUID postId,
            Pageable pageable
    );

    Page<CommentResponse> getReplies(
            UUID commentId,
            Pageable pageable
    );

    long countComments(UUID postId);

    long countReplies(UUID commentId);
}