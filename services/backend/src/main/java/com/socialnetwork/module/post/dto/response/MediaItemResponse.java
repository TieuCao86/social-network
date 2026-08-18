package com.socialnetwork.module.post.dto.response;

import com.socialnetwork.module.post.entity.enums.MediaType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class MediaItemResponse {

    private UUID id;
    private UUID fileId;
    private MediaType type;
    private int sortOrder;

    private long reactionCount;
}
