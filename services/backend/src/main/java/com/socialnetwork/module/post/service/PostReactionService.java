package com.socialnetwork.module.post.service;

import com.socialnetwork.module.post.dto.response.ReactionResponse;
import com.socialnetwork.module.post.dto.response.ReactionUserResponse;
import com.socialnetwork.module.post.entity.enums.ReactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PostReactionService {

    ReactionResponse toggleReaction(UUID postId, UUID currentUserId, ReactionType type);

    Page<ReactionUserResponse> getPostReactions(UUID postId, ReactionType type, Pageable pageable);
}