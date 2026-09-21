package com.socialnetwork.module.user.dto.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateRequest {

    private String fullName;

    private String bio;

    private String website;

    private String location;

    private LocalDate birthDate;
}