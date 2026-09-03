package com.socialnetwork.module.post.dto.response;

import com.socialnetwork.module.post.entity.enums.PostVisibility;
import com.socialnetwork.module.post.entity.enums.ReactionType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PostResponse {

    private UUID id;

    private PostAuthorResponse author;

    private String content;
    private PostVisibility visibility;

    private long totalReactions;
    private long commentCount;
    private long shareCount;

    private ReactionType currentUserReaction;

    private List<ReactionType> topReactions;

    private List<MediaItemResponse> mediaList;

    private Instant createdAt;
    private Instant updatedAt;
}