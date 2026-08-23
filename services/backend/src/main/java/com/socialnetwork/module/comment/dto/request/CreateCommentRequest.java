package com.socialnetwork.module.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentRequest {

    @NotBlank(message = "Nội dung bình luận không được để trống.")
    @Size(
            max = 5000,
            message = "Nội dung bình luận không được vượt quá 5000 ký tự."
    )
    private String content;

    private UUID parentId;
}