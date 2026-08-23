package com.socialnetwork.module.comment.dto.response;

import com.socialnetwork.module.comment.entity.CommentStatus;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private UUID id;

    private UUID postId;

    private UUID userId;

    private UUID parentId;

    private String content;

    private CommentStatus status;

    private Instant createdAt;

    private Instant updatedAt;
}