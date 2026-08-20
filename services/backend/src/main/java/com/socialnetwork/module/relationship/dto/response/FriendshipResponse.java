package com.socialnetwork.module.relationship.dto.response;

import com.socialnetwork.module.relationship.entity.enums.FriendshipStatus;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendshipResponse {

    private UUID id;

    private UUID requesterId;

    private UUID addresseeId;

    private FriendshipStatus status;

    private Instant createdAt;

    private Instant updatedAt;
}