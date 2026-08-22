package com.socialnetwork.module.relationship.entity;

import com.socialnetwork.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "follows")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Follow extends BaseEntity {

    @Column(name = "follower_id", nullable = false)
    private UUID followerId;

    @Column(name = "following_id", nullable = false)
    private UUID followingId;
}
