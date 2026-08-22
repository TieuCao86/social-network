package com.socialnetwork.module.relationship.service;

import com.socialnetwork.module.relationship.dto.response.FollowResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FollowService {

    FollowResponse follow(
            UUID currentUserId,
            UUID targetUserId
    );

    void unfollow(
            UUID currentUserId,
            UUID targetUserId
    );

    boolean isFollowing(
            UUID currentUserId,
            UUID targetUserId
    );

    Page<FollowResponse> getFollowing(
            UUID currentUserId,
            Pageable pageable
    );

    Page<FollowResponse> getFollowers(
            UUID currentUserId,
            Pageable pageable
    );

    long countFollowing(UUID userId);

    long countFollowers(UUID userId);
}