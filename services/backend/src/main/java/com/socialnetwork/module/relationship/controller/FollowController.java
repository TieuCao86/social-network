package com.socialnetwork.module.relationship.controller;

import com.socialnetwork.common.response.ApiResponse;
import com.socialnetwork.common.security.CustomUserDetails;
import com.socialnetwork.module.relationship.dto.response.FollowResponse;
import com.socialnetwork.module.relationship.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/relationships/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    // ============================================================
    // FOLLOW
    // ============================================================

    /**
     * Theo dõi người dùng
     * POST /api/relationships/follows/{targetUserId}
     */
    @PostMapping("/{targetUserId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<FollowResponse> follow(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID targetUserId
    ) {
        UUID currentUserId = userDetails.getUser().getId();

        FollowResponse response =
                followService.follow(
                        currentUserId,
                        targetUserId
                );

        return ApiResponse.success(
                "Theo dõi người dùng thành công.",
                response
        );
    }

    // ============================================================
    // UNFOLLOW
    // ============================================================

    /**
     * Bỏ theo dõi người dùng
     * DELETE /api/relationships/follows/{targetUserId}
     */
    @DeleteMapping("/{targetUserId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> unfollow(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID targetUserId
    ) {
        UUID currentUserId = userDetails.getUser().getId();

        followService.unfollow(
                currentUserId,
                targetUserId
        );

        return ApiResponse.success(
                "Đã bỏ theo dõi người dùng."
        );
    }

    // ============================================================
    // CHECK FOLLOWING
    // ============================================================

    /**
     * Kiểm tra current user có đang follow target user không
     * GET /api/relationships/follows/{targetUserId}/status
     */
    @GetMapping("/{targetUserId}/status")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Boolean> isFollowing(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID targetUserId
    ) {
        UUID currentUserId = userDetails.getUser().getId();

        boolean following =
                followService.isFollowing(
                        currentUserId,
                        targetUserId
                );

        return ApiResponse.success(following);
    }

    // ============================================================
    // GET FOLLOWING
    // ============================================================

    /**
     * Lấy danh sách người mình đang follow
     * GET /api/relationships/follows/following?page=0&size=20
     */
    @GetMapping("/following")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Page<FollowResponse>> getFollowing(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable
    ) {
        UUID currentUserId = userDetails.getUser().getId();

        Page<FollowResponse> response =
                followService.getFollowing(
                        currentUserId,
                        pageable
                );

        return ApiResponse.success(response);
    }

    // ============================================================
    // GET FOLLOWERS
    // ============================================================

    /**
     * Lấy danh sách người đang follow mình
     * GET /api/relationships/follows/followers?page=0&size=20
     */
    @GetMapping("/followers")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Page<FollowResponse>> getFollowers(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable
    ) {
        UUID currentUserId = userDetails.getUser().getId();

        Page<FollowResponse> response =
                followService.getFollowers(
                        currentUserId,
                        pageable
                );

        return ApiResponse.success(response);
    }

    // ============================================================
    // COUNT FOLLOWING
    // ============================================================

    /**
     * Đếm số người mình đang follow
     * GET /api/relationships/follows/following/count
     */
    @GetMapping("/following/count")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Long> countFollowing(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID currentUserId = userDetails.getUser().getId();

        long count =
                followService.countFollowing(currentUserId);

        return ApiResponse.success(count);
    }

    // ============================================================
    // COUNT FOLLOWERS
    // ============================================================

    /**
     * Đếm số người đang follow mình
     * GET /api/relationships/follows/followers/count
     */
    @GetMapping("/followers/count")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Long> countFollowers(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID currentUserId = userDetails.getUser().getId();

        long count =
                followService.countFollowers(currentUserId);

        return ApiResponse.success(count);
    }
}