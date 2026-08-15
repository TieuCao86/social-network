package com.socialnetwork.module.post.dto.response;

import com.socialnetwork.module.post.entity.enums.ReactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Kết quả sau khi tương tác cảm xúc bài viết")
public class ReactionResponse {

    @Schema(description = "Trạng thái tương tác: true nếu còn cảm xúc, false nếu vừa hủy (unlike)", example = "true")
    private boolean reacted;

    @Schema(description = "Loại cảm xúc hiện tại của user đang đăng nhập (null nếu vừa hủy)", example = "LOVE")
    private ReactionType currentUserReaction;

    @Schema(description = "Tổng số lượt thả cảm xúc trên bài viết", example = "15")
    private long totalReactions;

    @Schema(description = "Thống kê chi tiết số lượng theo từng loại cảm xúc")
    private Map<ReactionType, Long> reactionCounts;
}