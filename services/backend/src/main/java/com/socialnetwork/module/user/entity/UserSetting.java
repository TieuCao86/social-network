package com.socialnetwork.module.user.entity;

import com.socialnetwork.common.entity.BaseChildEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "user_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserSetting extends BaseChildEntity {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Builder.Default
    @Column(nullable = false)
    private boolean privateAccount = false;

    @Builder.Default
    @Column(nullable = false)
    private boolean allowTagging = true;

    @Builder.Default
    @Column(nullable = false, length = 10)
    private String language = "vi";

    @Builder.Default
    @Column(nullable = false, length = 20)
    private String theme = "SYSTEM";
}