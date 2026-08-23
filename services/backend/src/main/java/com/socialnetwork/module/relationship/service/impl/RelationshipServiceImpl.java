package com.socialnetwork.module.relationship.service.impl;

import com.socialnetwork.common.exception.BusinessException;
import com.socialnetwork.common.exception.ErrorCode;
import com.socialnetwork.module.relationship.dto.response.RelationshipResponse;
import com.socialnetwork.module.relationship.entity.Follow;
import com.socialnetwork.module.relationship.entity.Friendship;
import com.socialnetwork.module.relationship.mapper.RelationshipMapper;
import com.socialnetwork.module.relationship.repository.FollowRepository;
import com.socialnetwork.module.relationship.repository.FriendshipRepository;
import com.socialnetwork.module.relationship.service.RelationshipService;
import com.socialnetwork.module.relationship.util.RelationshipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RelationshipServiceImpl implements RelationshipService {

    private final FriendshipRepository friendshipRepository;
    private final FollowRepository followRepository;
    private final RelationshipMapper relationshipMapper;

    @Override
    public RelationshipResponse getRelationship(UUID currentUserId, UUID targetUserId) {
        RelationshipValidator.validateNotSelf(
                currentUserId,
                targetUserId,
                ErrorCode.CANNOT_FRIEND_SELF
        );

        Friendship friendship = friendshipRepository.findBetween(currentUserId, targetUserId)
                .orElse(null);

        Follow currentUserFollow = followRepository.findByFollowerIdAndFollowingId(currentUserId, targetUserId)
                .orElse(null);

        Follow targetUserFollow = followRepository.findByFollowerIdAndFollowingId(targetUserId, currentUserId)
                .orElse(null);

        return relationshipMapper.toResponse(
                currentUserId,
                targetUserId,
                friendship,
                currentUserFollow,
                targetUserFollow
        );
    }

}