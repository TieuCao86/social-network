package com.socialnetwork.module.relationship.mapper;

import com.socialnetwork.module.relationship.dto.response.RelationshipResponse;
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
            expression = "java(toRelationshipStatus(currentUserId, friendship))"
    )
    RelationshipResponse toResponse(
            UUID currentUserId,
            UUID targetUserId,
            Friendship friendship
    );

    default RelationshipStatus toRelationshipStatus(
            UUID currentUserId,
            Friendship friendship
    ) {
        if (friendship == null || friendship.getStatus() == null) {
            return RelationshipStatus.NONE;
        }

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
}