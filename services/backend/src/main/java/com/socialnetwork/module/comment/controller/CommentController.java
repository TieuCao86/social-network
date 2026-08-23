package com.socialnetwork.module.comment.controller;

import com.socialnetwork.common.response.ApiResponse;
import com.socialnetwork.common.security.CustomUserDetails;
import com.socialnetwork.module.comment.dto.request.CreateCommentRequest;
import com.socialnetwork.module.comment.dto.request.UpdateCommentRequest;
import com.socialnetwork.module.comment.dto.response.CommentResponse;
import com.socialnetwork.module.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // ============================================================
    // CREATE COMMENT
    // ============================================================

    @PostMapping("/posts/{postId}")
    public ApiResponse<CommentResponse> createComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID postId,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        CommentResponse response = commentService.createComment(
                userDetails.getUser().getId(),
                postId,
                request
        );

        return ApiResponse.success(
                "Bình luận thành công.",
                response
        );
    }

    // ============================================================
    // GET COMMENTS
    // ============================================================

    @GetMapping("/posts/{postId}")
    public ApiResponse<Page<CommentResponse>> getComments(
            @PathVariable UUID postId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<CommentResponse> response =
                commentService.getComments(postId, pageable);

        return ApiResponse.success(
                "Lấy danh sách bình luận thành công.",
                response
        );
    }

    // ============================================================
    // GET REPLIES
    // ============================================================

    @GetMapping("/{commentId}/replies")
    public ApiResponse<Page<CommentResponse>> getReplies(
            @PathVariable UUID commentId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<CommentResponse> response =
                commentService.getReplies(commentId, pageable);

        return ApiResponse.success(
                "Lấy danh sách phản hồi thành công.",
                response
        );
    }

    @GetMapping("/{commentId}/replies/count")
    public ApiResponse<Long> countReplies(
            @PathVariable UUID commentId
    ) {
        long count = commentService.countReplies(commentId);

        return ApiResponse.success(
                "Đếm số lượng phản hồi thành công.",
                count
        );
    }

    // ============================================================
    // COUNT COMMENTS
    // ============================================================

    @GetMapping("/posts/{postId}/count")
    public ApiResponse<Long> countComments(
            @PathVariable UUID postId
    ) {
        long count = commentService.countComments(postId);

        return ApiResponse.success(
                "Đếm số lượng bình luận thành công.",
                count
        );
    }

    // ============================================================
    // UPDATE COMMENT
    // ============================================================

    @PutMapping("/{commentId}")
    public ApiResponse<CommentResponse> updateComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID commentId,
            @Valid @RequestBody UpdateCommentRequest request
    ) {
        CommentResponse response = commentService.updateComment(
                userDetails.getUser().getId(),
                commentId,
                request
        );

        return ApiResponse.success(
                "Cập nhật bình luận thành công.",
                response
        );
    }

    // ============================================================
    // DELETE COMMENT
    // ============================================================

    @DeleteMapping("/{commentId}")
    public ApiResponse<Void> deleteComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID commentId
    ) {
        commentService.deleteComment(
                userDetails.getUser().getId(),
                commentId
        );

        return ApiResponse.success(
                "Xóa bình luận thành công.",
                null
        );
    }
}