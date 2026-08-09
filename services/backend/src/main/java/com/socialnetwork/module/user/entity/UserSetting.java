package com.socialnetwork.module.user.entity;

import com.socialnetwork.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "user_settings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSetting extends BaseEntity {

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "private_account", nullable = false)
    @Builder.Default
    private boolean privateAccount = false;

    @Column(name = "allow_tagging", nullable = false)
    @Builder.Default
    private boolean allowTagging = true;

    @Column(nullable = false, length = 10)
    @Builder.Default
    private String language = "vi";

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String theme = "SYSTEM";
}