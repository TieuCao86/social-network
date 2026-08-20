package com.socialnetwork.module.relationship.dto.response;

import com.socialnetwork.module.relationship.entity.enums.FriendshipStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class FriendshipStatusResponse {

    private UUID userId;

    private FriendshipStatus status;

    private boolean requester;
}