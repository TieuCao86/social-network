package com.socialnetwork.module.post.dto.request;

import com.socialnetwork.module.post.entity.enums.ReactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Yêu cầu bày tỏ hoặc thay đổi cảm xúc bài viết")
public class PostReactionRequest {

    @NotNull(message = "Loại cảm xúc không được để trống")
    @Schema(description = "Loại cảm xúc muốn thả", example = "LIKE")
    private ReactionType type;
}