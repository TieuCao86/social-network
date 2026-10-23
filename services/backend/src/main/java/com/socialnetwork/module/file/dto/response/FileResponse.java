package com.socialnetwork.module.file.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class FileResponse {

    private UUID fileId;
    private String originalName;
    private String contentType;
    private long sizeBytes;
    private String url;
    private Instant createdAt;
}