package com.socialnetwork.module.auth.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private UUID userId;

    private String username;

    private String accessToken;
}