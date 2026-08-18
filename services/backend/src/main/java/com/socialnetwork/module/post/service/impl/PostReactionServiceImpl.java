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

    /**
     * Toggle reaction của user trên Post.
     *
     * Logic:
     * - Chưa có reaction        -> Tạo mới (+1 total)
     * - Đã có cùng type         -> Xóa (-1 total)
     * - Đã có khác type         -> Đổi type (total giữ nguyên)
     */
    @Override
    @Transactional
    public ReactionResponse toggleReaction(UUID postId, UUID currentUserId, ReactionType type) {
        // 1. Lấy Post và khóa bi quan (Pessimistic Lock)
        Post post = postRepository.findByIdAndStatusForUpdate(postId, PostStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        ReactionType targetType = (type != null) ? type : ReactionType.LIKE;

        // 2. Tìm reaction hiện tại của user
        Optional<PostReaction> existingOpt = postReactionRepository.findByPostIdAndUserId(postId, currentUserId);

        boolean reacted;
        ReactionType currentUserReaction;

        // 3. Xử lý trạng thái reaction
        if (existingOpt.isEmpty()) {
            PostReaction reaction = PostReaction.builder()
                    .postId(postId)
                    .userId(currentUserId)
                    .type(targetType)
                    .build();

            postReactionRepository.save(reaction);
            post.setReactionCount(post.getReactionCount() + 1);

            reacted = true;
            currentUserReaction = targetType;
        } else {
            PostReaction existing = existingOpt.get();

            if (existing.getType() == targetType) {
                // Bấm lại cùng type -> Xóa
                postReactionRepository.delete(existing);
                post.setReactionCount(Math.max(0, post.getReactionCount() - 1));

                reacted = false;
                currentUserReaction = null;
            } else {
                // Bấm khác type -> Cập nhật type
                existing.setType(targetType);
                postReactionRepository.save(existing);

                reacted = true;
                currentUserReaction = targetType;
            }
        }

        // 4. Tổng hợp reaction summary theo type
        List<PostReactionRepository.ReactionCountProjection> countList =
                postReactionRepository.countReactionsByPostIdGroupedByType(postId);

        Map<ReactionType, Long> reactionCounts = new EnumMap<>(ReactionType.class);
        for (var item : countList) {
            reactionCounts.put(item.getType(), item.getCount());
        }

        return ReactionResponse.builder()
                .reacted(reacted)
                .currentUserReaction(currentUserReaction)
                .totalReactions(post.getReactionCount())
                .reactionCounts(reactionCounts)
                .build();
    }

    /**
     * Lấy danh sách user đã reaction Post (hỗ trợ phân trang và lọc theo type).
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ReactionUserResponse> getPostReactions(UUID postId, ReactionType type, Pageable pageable) {
        // 1. Kiểm tra Post tồn tại và ACTIVE
        if (!postRepository.existsByIdAndStatus(postId, PostStatus.ACTIVE)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        // 2. Query reactions
        Page<PostReaction> reactionsPage = (type == null)
                ? postReactionRepository.findByPostIdOrderByCreatedAtDesc(postId, pageable)
                : postReactionRepository.findByPostIdAndTypeOrderByCreatedAtDesc(postId, type, pageable);

        if (reactionsPage.isEmpty()) {
            return Page.empty(pageable);
        }

        // 3. Batch load User để tránh lỗi N+1 query
        List<UUID> userIds = reactionsPage.getContent().stream()
                .map(PostReaction::getUserId)
                .distinct()
                .toList();

        Map<UUID, User> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        // 4. Map DTO
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