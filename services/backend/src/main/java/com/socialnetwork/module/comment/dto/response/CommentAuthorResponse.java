package com.socialnetwork.module.comment.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentAuthorResponse {

    private UUID id;

    private String username;

    private String fullName;

    private UUID avatarFileId;
}