package com.socialnetwork.module.relationship.dto.response;

import com.socialnetwork.module.relationship.entity.enums.RelationshipStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class RelationshipSummaryResponse {
    private UUID targetUserId;
    private RelationshipStatus relationshipStatus;
}