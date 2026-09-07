package com.socialnetwork.module.comment.dto.response;

import com.socialnetwork.module.post.entity.enums.MediaType;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentMediaResponse {

    private UUID id;

    private UUID fileId;

    private MediaType type;

    private int sortOrder;
}