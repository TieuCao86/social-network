package com.socialnetwork.module.user.dto.response;

import com.socialnetwork.module.relationship.entity.enums.RelationshipStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UserSearchResponse {

    private UUID userId;

    private String username;

    private String fullName;

    private UUID avatarFileId;

    private RelationshipStatus relationshipStatus;
}