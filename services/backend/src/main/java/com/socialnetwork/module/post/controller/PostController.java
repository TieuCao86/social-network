package com.socialnetwork.module.post.controller;

import com.socialnetwork.common.exception.BusinessException;
import com.socialnetwork.common.exception.ErrorCode;
import com.socialnetwork.common.response.ApiResponse;
import com.socialnetwork.common.security.CustomUserDetails;
import com.socialnetwork.module.post.dto.request.PostCreateRequest;
import com.socialnetwork.module.post.dto.response.PostResponse;
import com.socialnetwork.module.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "Post Domain", description = "APIs quản lý bài viết và media đi kèm")
public class PostController {

    private final PostService postService;

    @Operation(summary = "Tạo bài viết mới (kèm media nếu có)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<PostResponse> createPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PostCreateRequest request
    ) {
        if (userDetails == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        UUID currentUserId = userDetails.getUser().getId();
        PostResponse response = postService.createPost(currentUserId, request);
        return ApiResponse.success("Tạo bài viết thành công", response);
    }

    @Operation(summary = "Lấy chi tiết bài viết theo ID")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<PostResponse> getPostById(@PathVariable UUID id) {
        PostResponse response = postService.getPostById(id);
        return ApiResponse.success(response);
    }

    @Operation(summary = "Lấy danh sách bài viết của 1 người dùng (có phân trang)")
    @GetMapping("/user/{authorId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Page<PostResponse>> getUserPosts(
            @PathVariable UUID authorId,
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<PostResponse> response = postService.getUserPosts(authorId, pageable);
        return ApiResponse.success(response);
    }

    @Operation(summary = "Xóa bài viết (Soft Delete - Dành cho chính chủ)")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> deletePost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID id
    ) {
        if (userDetails == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        UUID currentUserId = userDetails.getUser().getId();
        postService.deletePost(id, currentUserId);
        return ApiResponse.success("Xóa bài viết thành công", null);
    }
}