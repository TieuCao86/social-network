package com.socialnetwork.module.relationship.dto.response;

import com.socialnetwork.module.relationship.entity.enums.RelationshipStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelationshipResponse {

    private UUID userId;

    private RelationshipStatus relationshipStatus;
}