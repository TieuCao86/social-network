package com.socialnetwork.module.relationship.dto.response;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowResponse {

    private UUID id;

    private UUID followerId;

    private UUID followingId;

    private Instant createdAt;

    private Instant updatedAt;
}