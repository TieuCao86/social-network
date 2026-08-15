package com.socialnetwork.module.post.dto.response;

import com.socialnetwork.module.post.entity.enums.MediaType;
import com.socialnetwork.module.post.entity.enums.PostVisibility;
import com.socialnetwork.module.post.entity.enums.ReactionType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class PostResponse {

    private UUID id;
    private UUID authorId;
    private String content;
    private PostVisibility visibility;
    private int commentCount;
    private int likeCount;

    private List<MediaItemResponse> mediaList;

    private long totalReactions;                      // Tổng số lượt reaction
    private ReactionType currentUserReaction;         // User hiện tại đã thả gì (LIKE/null/...)
    private Map<ReactionType, Long> reactionSummary;

    private Instant createdAt;
    private Instant updatedAt;

    @Data
    @Builder
    public static class MediaItemResponse {
        private UUID id;
        private UUID fileId;
        private MediaType type;
        private int sortOrder;
    }
}