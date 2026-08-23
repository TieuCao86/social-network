package com.socialnetwork.module.relationship.mapper;

import com.socialnetwork.module.relationship.dto.response.RelationshipResponse;
import com.socialnetwork.module.relationship.entity.Follow;
import com.socialnetwork.module.relationship.entity.Friendship;
import com.socialnetwork.module.relationship.entity.enums.RelationshipStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface RelationshipMapper {

    @Mapping(target = "userId", source = "targetUserId")
    @Mapping(
            target = "relationshipStatus",
            expression = "java(toRelationshipStatus(currentUserId, friendship, currentUserFollow, targetUserFollow))"
    )
    RelationshipResponse toResponse(
            UUID currentUserId,
            UUID targetUserId,
            Friendship friendship,
            Follow currentUserFollow,
            Follow targetUserFollow
    );

    default RelationshipStatus toRelationshipStatus(
            UUID currentUserId,
            Friendship friendship,
            Follow currentUserFollow,
            Follow targetUserFollow
    ) {

        // ============================================================
        // 1. FRIENDSHIP ƯU TIÊN
        // ============================================================

        if (friendship != null) {

            return switch (friendship.getStatus()) {

                case ACCEPTED ->
                        RelationshipStatus.FRIENDS;

                case PENDING ->
                        friendship.getRequesterId().equals(currentUserId)
                                ? RelationshipStatus.REQUEST_SENT
                                : RelationshipStatus.REQUEST_RECEIVED;

                case BLOCKED ->
                        friendship.getRequesterId().equals(currentUserId)
                                ? RelationshipStatus.BLOCKING
                                : RelationshipStatus.BLOCKED_BY;
            };
        }

        // ============================================================
        // 2. FOLLOW
        // ============================================================

        boolean following = currentUserFollow != null;
        boolean followedBy = targetUserFollow != null;

        if (following && followedBy) {
            return RelationshipStatus.FOLLOWING_EACH_OTHER;
        }

        if (following) {
            return RelationshipStatus.FOLLOWING;
        }

        if (followedBy) {
            return RelationshipStatus.FOLLOWED_BY;
        }

        // ============================================================
        // 3. NONE
        // ============================================================

        return RelationshipStatus.NONE;
    }
}