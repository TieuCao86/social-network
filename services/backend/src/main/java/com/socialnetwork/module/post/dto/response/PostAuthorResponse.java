package com.socialnetwork.module.post.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PostAuthorResponse {

    private UUID userId;

    private String username;

    private String fullName;

    private UUID avatarFileId;
}