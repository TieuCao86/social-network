package com.socialnetwork.module.post.service.impl;

import com.socialnetwork.common.exception.BusinessException;
import com.socialnetwork.common.exception.ErrorCode;
import com.socialnetwork.module.post.dto.response.ReactionResponse;
import com.socialnetwork.module.post.dto.response.ReactionUserResponse;
import com.socialnetwork.module.post.entity.Post;
import com.socialnetwork.module.post.entity.PostReaction;
import com.socialnetwork.module.post.entity.enums.PostStatus;
import com.socialnetwork.module.post.entity.enums.ReactionType;
import com.socialnetwork.module.post.repository.PostReactionRepository;
import com.socialnetwork.module.post.repository.PostRepository;
import com.socialnetwork.module.post.service.PostReactionService;
import com.socialnetwork.module.user.entity.User;
import com.socialnetwork.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostReactionServiceImpl implements PostReactionService {

    private final PostRepository postRepository;
    private final PostReactionRepository postReactionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ReactionResponse toggleReaction(UUID postId, UUID currentUserId, ReactionType type) {
        // 1. Kiểm tra bài viết tồn tại & ACTIVE
        Post post = postRepository.findById(postId)
                .filter(p -> p.getStatus() == PostStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        Optional<PostReaction> existingOpt = postReactionRepository.findByPostIdAndUserId(postId, currentUserId);

        boolean isReacted;
        ReactionType activeType = null;
        ReactionType targetType = (type != null) ? type : ReactionType.LIKE;

        if (existingOpt.isPresent()) {
            PostReaction existing = existingOpt.get();
            if (existing.getType() == targetType) {
                // Bấm lại chính icon -> Hủy reaction
                postReactionRepository.delete(existing);
                isReacted = false;
            } else {
                // Đổi icon khác
                existing.setType(targetType);
                postReactionRepository.save(existing);
                isReacted = true;
                activeType = targetType;
            }
        } else {
            // Thả reaction mới
            PostReaction newReaction = PostReaction.builder()
                    .postId(postId)
                    .userId(currentUserId)
                    .type(targetType)
                    .build();
            postReactionRepository.save(newReaction);
            isReacted = true;
            activeType = targetType;
        }

        // 2. Thống kê chi tiết
        List<PostReactionRepository.ReactionCountProjection> countList =
                postReactionRepository.countReactionsByPostIdGroupedByType(postId);

        Map<ReactionType, Long> reactionCounts = new EnumMap<>(ReactionType.class);
        long total = 0;
        for (var item : countList) {
            reactionCounts.put(item.getType(), item.getCount());
            total += item.getCount();
        }

        return ReactionResponse.builder()
                .reacted(isReacted)
                .currentUserReaction(activeType)
                .totalReactions(total)
                .reactionCounts(reactionCounts)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReactionUserResponse> getPostReactions(UUID postId, ReactionType type, Pageable pageable) {
        if (!postRepository.existsById(postId)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        Page<PostReaction> reactionsPage = (type == null)
                ? postReactionRepository.findByPostIdOrderByCreatedAtDesc(postId, pageable)
                : postReactionRepository.findByPostIdAndTypeOrderByCreatedAtDesc(postId, type, pageable);

        if (reactionsPage.isEmpty()) {
            return Page.empty(pageable);
        }

        List<UUID> userIds = reactionsPage.getContent().stream()
                .map(PostReaction::getUserId)
                .distinct()
                .toList();

        Map<UUID, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        return reactionsPage.map(reaction -> {
            User user = userMap.get(reaction.getUserId());
            return ReactionUserResponse.builder()
                    .id(reaction.getId())
                    .userId(reaction.getUserId())
                    .username(user != null ? user.getUsername() : "Unknown")
                    .email(user != null ? user.getEmail() : null)
                    .reactionType(reaction.getType())
                    .createdAt(reaction.getCreatedAt())
                    .build();
        });
    }
}