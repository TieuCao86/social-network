package com.socialnetwork.module.relationship.controller;

import com.socialnetwork.common.response.ApiResponse;
import com.socialnetwork.common.security.CustomUserDetails;
import com.socialnetwork.module.relationship.dto.response.FriendshipResponse;
import com.socialnetwork.module.relationship.service.FriendshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/relationships")
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;

    /**
     * Gửi lời mời kết bạn
     * POST /api/relationships/friends/{targetUserId}
     */
    @PostMapping("/friends/{targetUserId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<FriendshipResponse> sendRequest(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID targetUserId
    ) {
        UUID currentUserId = userDetails.getUser().getId();

        FriendshipResponse data =
                friendshipService.sendRequest(
                        currentUserId,
                        targetUserId
                );

        return ApiResponse.success(
                "Gửi lời mời kết bạn thành công.",
                data
        );
    }

    /**
     * Chấp nhận lời mời kết bạn
     * POST /api/relationships/friends/{requesterId}/accept
     */
    @PostMapping("/friends/{requesterId}/accept")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<FriendshipResponse> acceptRequest(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID requesterId
    ) {
        UUID currentUserId = userDetails.getUser().getId();

        FriendshipResponse data =
                friendshipService.acceptRequest(
                        currentUserId,
                        requesterId
                );

        return ApiResponse.success(
                "Đã chấp nhận lời mời kết bạn.",
                data
        );
    }

    /**
     * Xóa lời mời kết bạn nhận được
     * DELETE /api/relationships/friends/{requesterId}/request
     */
    @DeleteMapping("/friends/{requesterId}/request")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> rejectRequest(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID requesterId
    ) {
        UUID currentUserId = userDetails.getUser().getId();

        friendshipService.rejectRequest(
                currentUserId,
                requesterId
        );

        return ApiResponse.success(
                "Đã xóa lời mời kết bạn."
        );
    }

    /**
     * Hủy lời mời kết bạn đã gửi
     * DELETE /api/relationships/friends/{targetUserId}/cancel
     */
    @DeleteMapping("/friends/{targetUserId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> cancelRequest(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID targetUserId
    ) {
        UUID currentUserId = userDetails.getUser().getId();

        friendshipService.cancelRequest(
                currentUserId,
                targetUserId
        );

        return ApiResponse.success(
                "Đã hủy lời mời kết bạn."
        );
    }

    /**
     * Hủy kết bạn
     * DELETE /api/relationships/friends/{friendId}
     */
    @DeleteMapping("/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> unfriend(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID friendId
    ) {
        UUID currentUserId = userDetails.getUser().getId();

        friendshipService.unfriend(
                currentUserId,
                friendId
        );

        return ApiResponse.success(
                "Đã hủy kết bạn."
        );
    }

    /**
     * Lấy danh sách bạn bè
     * GET /api/relationships/friends?page=0&size=20
     */
    @GetMapping("/friends")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Page<FriendshipResponse>> getFriends(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable
    ) {
        UUID currentUserId = userDetails.getUser().getId();

        Page<FriendshipResponse> data =
                friendshipService.getFriends(
                        currentUserId,
                        pageable
                );

        return ApiResponse.success(data);
    }


    /**
     * Lấy lời mời kết bạn nhận được
     * GET /api/relationships/friends/requests/received
     */
    @GetMapping("/friends/requests/received")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Page<FriendshipResponse>> getReceivedRequests(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable
    ) {
        UUID currentUserId = userDetails.getUser().getId();

        Page<FriendshipResponse> data =
                friendshipService.getReceivedRequests(
                        currentUserId,
                        pageable
                );

        return ApiResponse.success(data);
    }

    /**
     * Lấy lời mời kết bạn đã gửi
     * GET /api/relationships/friends/requests/sent
     */
    @GetMapping("/friends/requests/sent")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Page<FriendshipResponse>> getSentRequests(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Pageable pageable
    ) {
        UUID currentUserId = userDetails.getUser().getId();

        Page<FriendshipResponse> data =
                friendshipService.getSentRequests(
                        currentUserId,
                        pageable
                );

        return ApiResponse.success(data);
    }

    /**
     * Đếm số lượng bạn bè
     * GET /api/relationships/friends/count
     */
    @GetMapping("/friends/count")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Long> countFriends(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UUID currentUserId = userDetails.getUser().getId();

        long data =
                friendshipService.countFriends(currentUserId);

        return ApiResponse.success(data);
    }
}