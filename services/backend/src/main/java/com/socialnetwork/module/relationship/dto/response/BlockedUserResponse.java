package com.socialnetwork.module.relationship.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class BlockedUserResponse {

    private UUID userId;

    private String username;

}
