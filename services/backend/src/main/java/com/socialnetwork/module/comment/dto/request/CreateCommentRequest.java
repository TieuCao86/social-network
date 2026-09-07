package com.socialnetwork.module.comment.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentRequest {

    @Size(
            max = 5000,
            message = "Nội dung bình luận không được vượt quá 5000 ký tự."
    )
    private String content;

    private UUID parentId;

    @Valid
    private List<CommentMediaRequest> mediaList;
}