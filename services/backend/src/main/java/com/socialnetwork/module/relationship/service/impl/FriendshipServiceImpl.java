package com.socialnetwork.module.relationship.service.impl;

import com.socialnetwork.common.exception.BusinessException;
import com.socialnetwork.common.exception.ErrorCode;
import com.socialnetwork.module.relationship.dto.response.FriendshipResponse;
import com.socialnetwork.module.relationship.dto.response.RelationshipResponse;
import com.socialnetwork.module.relationship.entity.Friendship;
import com.socialnetwork.module.relationship.entity.enums.FriendshipStatus;
import com.socialnetwork.module.relationship.entity.enums.RelationshipStatus;
import com.socialnetwork.module.relationship.mapper.FriendshipMapper;
import com.socialnetwork.module.relationship.mapper.RelationshipMapper;
import com.socialnetwork.module.relationship.repository.FriendshipRepository;
import com.socialnetwork.module.relationship.service.FriendshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendshipServiceImpl implements FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final FriendshipMapper friendshipMapper;
    private final RelationshipMapper relationshipMapper;

    // ============================================================
    // FRIEND REQUEST
    // ============================================================

    @Override
    @Transactional
    public FriendshipResponse sendRequest(UUID currentUserId, UUID targetUserId) {
        validateNotSelf(currentUserId, targetUserId);

        var existing = friendshipRepository.findBetween(currentUserId, targetUserId);
        if (existing.isPresent()) {
            Friendship friendship = existing.get();

            switch (friendship.getStatus()) {
                case ACCEPTED -> throw new BusinessException(ErrorCode.ALREADY_FRIENDS);

                case BLOCKED -> {
                    if (friendship.getRequesterId().equals(currentUserId)) {
                        throw new BusinessException(ErrorCode.BLOCKING_USER);
                    }
                    throw new BusinessException(ErrorCode.BLOCKED_BY_USER);
                }

                case PENDING -> {
                    if (friendship.getRequesterId().equals(currentUserId)) {
                        throw new BusinessException(ErrorCode.FRIEND_REQUEST_ALREADY_SENT);
                    }
                    throw new BusinessException(ErrorCode.FRIEND_REQUEST_ALREADY_RECEIVED);
                }
            }
        }

        Friendship friendship = Friendship.builder()
                .requesterId(currentUserId)
                .addresseeId(targetUserId)
                .status(FriendshipStatus.PENDING)
                .build();

        return friendshipMapper.toResponse(friendshipRepository.save(friendship));
    }

    // ============================================================
    // ACCEPT REQUEST
    // ============================================================

    @Override
    @Transactional
    public FriendshipResponse acceptRequest(UUID currentUserId, UUID requesterId) {
        validateNotSelf(currentUserId, requesterId);

        Friendship friendship = friendshipRepository.findBetween(currentUserId, requesterId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

        if (!friendship.getRequesterId().equals(requesterId) || !friendship.getAddresseeId().equals(currentUserId)) {
            throw new BusinessException(ErrorCode.FRIEND_REQUEST_NOT_FOUND);
        }

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_FRIEND_REQUEST_STATUS);
        }

        friendship.setStatus(FriendshipStatus.ACCEPTED);
        return friendshipMapper.toResponse(friendshipRepository.save(friendship));
    }

    // ============================================================
    // REJECT REQUEST
    // ============================================================

    @Override
    @Transactional
    public void rejectRequest(UUID currentUserId, UUID requesterId) {
        validateNotSelf(currentUserId, requesterId);

        Friendship friendship = friendshipRepository.findBetween(currentUserId, requesterId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

        if (!friendship.getRequesterId().equals(requesterId) || !friendship.getAddresseeId().equals(currentUserId)) {
            throw new BusinessException(ErrorCode.FRIEND_REQUEST_NOT_FOUND);
        }

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_FRIEND_REQUEST_STATUS);
        }

        friendshipRepository.delete(friendship);
    }

    // ============================================================
    // CANCEL REQUEST
    // ============================================================

    @Override
    @Transactional
    public void cancelRequest(UUID currentUserId, UUID targetUserId) {
        validateNotSelf(currentUserId, targetUserId);

        Friendship friendship = friendshipRepository.findBetween(currentUserId, targetUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));

        if (!friendship.getRequesterId().equals(currentUserId) || !friendship.getAddresseeId().equals(targetUserId)) {
            throw new BusinessException(ErrorCode.FRIEND_REQUEST_NOT_FOUND);
        }

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_FRIEND_REQUEST_STATUS);
        }

        friendshipRepository.delete(friendship);
    }

    // ============================================================
    // UNFRIEND
    // ============================================================

    @Override
    @Transactional
    public void unfriend(UUID currentUserId, UUID friendId) {
        validateNotSelf(currentUserId, friendId);

        Friendship friendship = friendshipRepository.findBetween(currentUserId, friendId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FRIENDSHIP_NOT_FOUND));

        if (friendship.getStatus() != FriendshipStatus.ACCEPTED) {
            throw new BusinessException(ErrorCode.NOT_FRIENDS);
        }

        friendshipRepository.delete(friendship);
    }

    // ============================================================
    // GET RELATIONSHIP
    // ============================================================

    @Override
    public RelationshipResponse getRelationship(UUID currentUserId, UUID targetUserId) {
        validateNotSelf(currentUserId, targetUserId);

        return friendshipRepository.findBetween(currentUserId, targetUserId)
                .map(friendship -> relationshipMapper.toResponse(currentUserId, targetUserId, friendship))
                .orElseGet(() -> RelationshipResponse.builder()
                        .userId(targetUserId)
                        .relationshipStatus(RelationshipStatus.NONE)
                        .build());
    }

    // ============================================================
    // QUERIES
    // ============================================================

    @Override
    public Page<FriendshipResponse> getFriends(UUID currentUserId, Pageable pageable) {
        return friendshipRepository.findAllByUserIdAndStatus(currentUserId, FriendshipStatus.ACCEPTED, pageable)
                .map(friendshipMapper::toResponse);
    }

    @Override
    public Page<FriendshipResponse> getReceivedRequests(UUID currentUserId, Pageable pageable) {
        return friendshipRepository.findByAddresseeIdAndStatus(currentUserId, FriendshipStatus.PENDING, pageable)
                .map(friendshipMapper::toResponse);
    }

    @Override
    public Page<FriendshipResponse> getSentRequests(UUID currentUserId, Pageable pageable) {
        return friendshipRepository.findByRequesterIdAndStatus(currentUserId, FriendshipStatus.PENDING, pageable)
                .map(friendshipMapper::toResponse);
    }

    @Override
    public long countFriends(UUID userId) {
        return friendshipRepository.countFriends(userId);
    }

    // ============================================================
    // PRIVATE METHODS
    // ============================================================

    private void validateNotSelf(UUID currentUserId, UUID targetUserId) {
        if (currentUserId.equals(targetUserId)) {
            throw new BusinessException(ErrorCode.CANNOT_FRIEND_SELF);
        }
    }
}