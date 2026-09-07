package com.socialnetwork.module.comment.dto.request;

import com.socialnetwork.module.post.entity.enums.MediaType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentMediaRequest {

    @NotNull(message = "File không được để trống.")
    private UUID fileId;

    @NotNull(message = "Loại media không được để trống.")
    private MediaType type;
}