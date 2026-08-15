package com.socialnetwork.module.post.dto.response;

import com.socialnetwork.module.post.entity.enums.ReactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thông tin người dùng đã bày tỏ cảm xúc")
public class ReactionUserResponse {

    @Schema(description = "ID của lượt reaction")
    private UUID id;

    @Schema(description = "ID người dùng")
    private UUID userId;

    @Schema(description = "Username người dùng", example = "john_doe")
    private String username;

    @Schema(description = "Email người dùng", example = "user@example.com")
    private String email;

    @Schema(description = "Loại cảm xúc đã chọn", example = "LIKE")
    private ReactionType reactionType;

    @Schema(description = "Thời điểm bày tỏ cảm xúc")
    private Instant createdAt;
}