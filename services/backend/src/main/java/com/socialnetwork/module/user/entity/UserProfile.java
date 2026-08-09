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

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile extends BaseEntity {

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "avatar_file_id")
    private UUID avatarFileId;

    @Column(name = "cover_file_id")
    private UUID coverFileId;

    @Column(length = 500)
    private String bio;

    @Column(length = 255)
    private String website;

    @Column(length = 255)
    private String location;

    @Column(name = "birth_date")
    private LocalDate birthDate;
}