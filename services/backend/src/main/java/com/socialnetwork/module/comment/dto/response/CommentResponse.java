package com.socialnetwork.module.comment.dto.response;

import com.socialnetwork.module.comment.entity.CommentStatus;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private UUID id;

    private UUID postId;

    private CommentAuthorResponse author;

    private UUID parentId;

    private String content;

    private CommentStatus status;

    private List<CommentMediaResponse> mediaList;

    private long replyCount;

    private Instant createdAt;

    private Instant updatedAt;
}