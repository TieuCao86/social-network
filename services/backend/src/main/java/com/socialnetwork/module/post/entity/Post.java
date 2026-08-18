package com.socialnetwork.module.post.entity;

import com.socialnetwork.common.entity.BaseEntity;
import com.socialnetwork.module.post.entity.enums.PostStatus;
import com.socialnetwork.module.post.entity.enums.PostVisibility;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Post extends BaseEntity {

    @Column(name = "author_id", nullable = false)
    private UUID authorId;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PostVisibility visibility = PostVisibility.PUBLIC;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PostStatus status = PostStatus.ACTIVE;

    @Builder.Default
    @Column(name = "comment_count", nullable = false)
    private long commentCount = 0;

    @Builder.Default
    @Column(name = "reaction_count", nullable = false)
    private long reactionCount = 0;
}