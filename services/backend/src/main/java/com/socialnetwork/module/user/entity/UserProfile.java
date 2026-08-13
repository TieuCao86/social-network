package com.socialnetwork.module.user.entity;

import com.socialnetwork.common.entity.BaseChildEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserProfile extends BaseChildEntity {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(length = 100)
    private String fullName;

    private UUID avatarFileId;
    private UUID coverFileId;

    @Column(length = 500)
    private String bio;

    private String website;
    private String location;
    private LocalDate birthDate;
}