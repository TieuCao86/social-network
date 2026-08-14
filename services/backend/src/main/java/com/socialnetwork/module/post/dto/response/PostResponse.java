package com.socialnetwork.module.post.dto.response;

import com.socialnetwork.module.post.entity.MediaType;
import com.socialnetwork.module.post.entity.PostVisibility;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
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

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @Builder
    public static class MediaItemResponse {
        private UUID id;
        private UUID fileId;
        private MediaType type;
        private int sortOrder;
    }
}