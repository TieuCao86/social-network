package com.socialnetwork.module.post.dto.request;

import com.socialnetwork.module.post.entity.enums.ReactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request thay đổi reaction của bài viết")
public class ReactionRequest {

    @Schema(description = "Loại reaction (LIKE, LOVE, HAHA, WOW, SAD, ANGRY). Mặc định là LIKE nếu để trống.", example = "LIKE")
    private ReactionType type;
}