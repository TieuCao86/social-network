package com.socialnetwork.module.comment.entity;

import com.socialnetwork.common.entity.BaseEntity;
import com.socialnetwork.module.post.entity.enums.MediaType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "comment_media")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CommentMedia extends BaseEntity {

    @Column(name = "comment_id", nullable = false)
    private UUID commentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MediaType type;

    @Column(name = "file_id", nullable = false)
    private UUID fileId;

    @Builder.Default
    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;
}
