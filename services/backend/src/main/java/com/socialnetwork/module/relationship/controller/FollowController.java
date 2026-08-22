package com.socialnetwork.module.relationship.controller;

import com.socialnetwork.common.response.ApiResponse;
import com.socialnetwork.common.security.CustomUserDetails;
import com.socialnetwork.module.relationship.dto.response.FollowResponse;
import com.socialnetwork.module.relationship.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    @PostMapping("/{targetUserId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<FollowResponse> follow(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID targetUserId
    ) {
        UUID currentUserId = userDetails.getUser().getId();

        FollowResponse response = followService.follow(
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

    @GetMapping("/{targetUserId}/status")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Boolean> isFollowing(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID targetUserId
    ) {
        UUID currentUserId = userDetails.getUser().getId();

        boolean following = followService.isFollowing(
                currentUserId,
                targetUserId
        );

        return ApiResponse.success(following);
    }

    // ============================================================
    // GET FOLLOWING
    // ============================================================

    @GetMapping("/following")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Page<FollowResponse>> getFollowing(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        UUID currentUserId = userDetails.getUser().getId();

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

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

    @GetMapping("/followers")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Page<FollowResponse>> getFollowers(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        UUID currentUserId = userDetails.getUser().getId();

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

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

    @GetMapping("/following/count")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Long> countFollowing(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID currentUserId = userDetails.getUser().getId();

        long count = followService.countFollowing(currentUserId);

        return ApiResponse.success(count);
    }

    // ============================================================
    // COUNT FOLLOWERS
    // ============================================================

    @GetMapping("/followers/count")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Long> countFollowers(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID currentUserId = userDetails.getUser().getId();

        long count = followService.countFollowers(currentUserId);

        return ApiResponse.success(count);
    }
}