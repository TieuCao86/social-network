package com.socialnetwork.module.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class AuthResult {

    private UUID userId;

    private String username;

    private String accessToken;
}
