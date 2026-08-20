package com.socialnetwork.module.relationship.service;

import com.socialnetwork.module.relationship.dto.response.FriendshipResponse;
import com.socialnetwork.module.relationship.dto.response.RelationshipResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FriendshipService {

    /**
     * Gửi lời mời kết bạn.
     */
    FriendshipResponse sendRequest(UUID currentUserId, UUID targetUserId);

    /**
     * Chấp nhận lời mời kết bạn.
     */
    FriendshipResponse acceptRequest(UUID currentUserId, UUID requesterId);

    /**
     * Từ chối lời mời kết bạn.
     */
    void rejectRequest(UUID currentUserId, UUID requesterId);

    /**
     * Hủy lời mời kết bạn đã gửi.
     */
    void cancelRequest(UUID currentUserId, UUID targetUserId);

    /**
     * Hủy kết bạn (Unfriend).
     */
    void unfriend(UUID currentUserId, UUID friendId);

    /**
     * Lấy trạng thái relationship giữa current user và target user.
     */
    RelationshipResponse getRelationship(UUID currentUserId, UUID targetUserId);

    /**
     * Lấy danh sách bạn bè.
     */
    Page<FriendshipResponse> getFriends(UUID currentUserId, Pageable pageable);

    /**
     * Lấy lời mời kết bạn nhận được.
     */
    Page<FriendshipResponse> getReceivedRequests(UUID currentUserId, Pageable pageable);

    /**
     * Lấy lời mời kết bạn đã gửi.
     */
    Page<FriendshipResponse> getSentRequests(UUID currentUserId, Pageable pageable);

    /**
     * Đếm số bạn bè.
     */
    long countFriends(UUID userId);
}