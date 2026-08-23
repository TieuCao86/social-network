package com.socialnetwork.module.relationship.service;

import com.socialnetwork.module.relationship.dto.response.FriendshipResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FriendshipService {

    FriendshipResponse sendRequest(
            UUID currentUserId,
            UUID targetUserId
    );

    FriendshipResponse acceptRequest(
            UUID currentUserId,
            UUID requesterId
    );

    void rejectRequest(
            UUID currentUserId,
            UUID requesterId
    );

    void cancelRequest(
            UUID currentUserId,
            UUID targetUserId
    );

    void unfriend(
            UUID currentUserId,
            UUID friendId
    );

    Page<FriendshipResponse> getFriends(
            UUID currentUserId,
            Pageable pageable
    );

    Page<FriendshipResponse> getReceivedRequests(
            UUID currentUserId,
            Pageable pageable
    );

    Page<FriendshipResponse> getSentRequests(
            UUID currentUserId,
            Pageable pageable
    );

    long countFriends(UUID userId);
}