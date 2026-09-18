package com.socialnetwork.module.relationship.service;

import com.socialnetwork.module.relationship.dto.response.RelationshipResponse;
import com.socialnetwork.module.relationship.entity.enums.RelationshipStatus;

import java.util.UUID;

public interface RelationshipService {

    RelationshipResponse getRelationship(
            UUID currentUserId,
            UUID targetUserId
    );

    RelationshipStatus getStatus(
            UUID currentUserId,
            UUID targetUserId
    );
}