package com.socialnetwork.module.relationship.service.impl;

import com.socialnetwork.common.exception.BusinessException;
import com.socialnetwork.common.exception.ErrorCode;
import com.socialnetwork.module.relationship.dto.response.FollowResponse;
import com.socialnetwork.module.relationship.entity.Follow;
import com.socialnetwork.module.relationship.mapper.FollowMapper;
import com.socialnetwork.module.relationship.repository.FollowRepository;
import com.socialnetwork.module.relationship.service.FollowService;
import com.socialnetwork.module.relationship.util.RelationshipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final FollowMapper followMapper;

    // ============================================================
    // FOLLOW
    // ============================================================

    @Override
    @Transactional
    public FollowResponse follow(
            UUID currentUserId,
            UUID targetUserId
    ) {
        RelationshipValidator.validateNotSelf(
                currentUserId,
                targetUserId,
                ErrorCode.CANNOT_FOLLOW_SELF
        );

        if (followRepository.existsByFollowerIdAndFollowingId(
                currentUserId,
                targetUserId
        )) {
            throw new BusinessException(
                    ErrorCode.ALREADY_FOLLOWING
            );
        }

        Follow follow = Follow.builder()
                .followerId(currentUserId)
                .followingId(targetUserId)
                .build();

        return followMapper.toResponse(
                followRepository.save(follow)
        );
    }

    // ============================================================
    // UNFOLLOW
    // ============================================================

    @Override
    @Transactional
    public void unfollow(
            UUID currentUserId,
            UUID targetUserId
    ) {
        RelationshipValidator.validateNotSelf(
                currentUserId,
                targetUserId,
                ErrorCode.CANNOT_FOLLOW_SELF
        );

        Follow follow = followRepository
                .findByFollowerIdAndFollowingId(
                        currentUserId,
                        targetUserId
                )
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.NOT_FOLLOWING
                        )
                );

        followRepository.delete(follow);
    }

    // ============================================================
    // CHECK FOLLOWING
    // ============================================================

    @Override
    public boolean isFollowing(
            UUID currentUserId,
            UUID targetUserId
    ) {
        if (currentUserId.equals(targetUserId)) {
            return false;
        }

        return followRepository.existsByFollowerIdAndFollowingId(
                currentUserId,
                targetUserId
        );
    }

    // ============================================================
    // GET FOLLOWING
    // ============================================================

    @Override
    public Page<FollowResponse> getFollowing(
            UUID currentUserId,
            Pageable pageable
    ) {
        return followRepository
                .findByFollowerId(
                        currentUserId,
                        pageable
                )
                .map(followMapper::toResponse);
    }

    // ============================================================
    // GET FOLLOWERS
    // ============================================================

    @Override
    public Page<FollowResponse> getFollowers(
            UUID currentUserId,
            Pageable pageable
    ) {
        return followRepository
                .findByFollowingId(
                        currentUserId,
                        pageable
                )
                .map(followMapper::toResponse);
    }

    // ============================================================
    // COUNT FOLLOWING
    // ============================================================

    @Override
    public long countFollowing(UUID userId) {
        return followRepository.countByFollowerId(userId);
    }

    // ============================================================
    // COUNT FOLLOWERS
    // ============================================================

    @Override
    public long countFollowers(UUID userId) {
        return followRepository.countByFollowingId(userId);
    }
}