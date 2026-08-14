package com.socialnetwork.module.post.dto.request;

import com.socialnetwork.module.post.entity.MediaType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class PostMediaRequest {

    @NotNull(message = "File ID không được để trống")
    private UUID fileId;

    @NotNull(message = "Loại media không được để trống")
    private MediaType type;
}