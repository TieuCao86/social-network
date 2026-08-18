package com.socialnetwork.module.post.controller;

import com.socialnetwork.common.exception.BusinessException;
import com.socialnetwork.common.exception.ErrorCode;
import com.socialnetwork.common.response.ApiResponse;
import com.socialnetwork.common.security.CustomUserDetails;
import com.socialnetwork.module.post.dto.request.ReactionRequest;
import com.socialnetwork.module.post.dto.response.ReactionResponse;
import com.socialnetwork.module.post.dto.response.ReactionUserResponse;
import com.socialnetwork.module.post.entity.enums.ReactionType;
import com.socialnetwork.module.post.service.PostReactionService;
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
@Tag(
        name = "Post Reaction Domain",
        description = "APIs quản lý reaction của bài viết"
)
public class PostReactionController {

    private final PostReactionService postReactionService;

    /**
     * React / Unreact / Đổi reaction
     *
     * POST /api/posts/{postId}/reactions
     */
    @Operation(
            summary = "Thả, hủy hoặc thay đổi reaction của bài viết"
    )
    @PostMapping("/{postId}/reactions")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ReactionResponse> toggleReaction(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID postId,
            @Valid @RequestBody ReactionRequest request
    ) {

        if (userDetails == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        UUID currentUserId = userDetails.getUser().getId();

        ReactionResponse response =
                postReactionService.toggleReaction(
                        postId,
                        currentUserId,
                        request.getType()
                );

        return ApiResponse.success(
                "Cập nhật reaction thành công",
                response
        );
    }

    /**
     * Lấy danh sách user đã reaction bài viết
     *
     * GET /api/posts/{postId}/reactions
     *
     * Có thể filter:
     *
     * GET /api/posts/{postId}/reactions?type=LOVE
     */
    @Operation(
            summary = "Lấy danh sách người đã reaction bài viết"
    )
    @GetMapping("/{postId}/reactions")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Page<ReactionUserResponse>> getPostReactions(
            @PathVariable UUID postId,

            @RequestParam(required = false)
            ReactionType type,

            @ParameterObject
            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {

        Page<ReactionUserResponse> response =
                postReactionService.getPostReactions(
                        postId,
                        type,
                        pageable
                );

        return ApiResponse.success(response);
    }
}