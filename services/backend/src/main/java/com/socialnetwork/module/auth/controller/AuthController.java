package com.socialnetwork.module.auth.controller;

import com.socialnetwork.common.response.ApiResponse;
import com.socialnetwork.module.auth.dto.request.LoginRequest;
import com.socialnetwork.module.auth.dto.response.AuthResult;
import com.socialnetwork.module.auth.dto.response.LoginResponse;
import com.socialnetwork.module.auth.service.AuthService;
import com.socialnetwork.module.user.dto.request.UserCreateRequest;
import com.socialnetwork.module.user.dto.response.UserResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            @RequestHeader(
                    value = "X-Client-Type",
                    defaultValue = "WEB"
            ) String clientType,
            HttpServletResponse response
    ) {

        AuthResult result = authService.login(request);

        LoginResponse.LoginResponseBuilder builder =
                LoginResponse.builder()
                        .userId(result.getUserId())
                        .username(result.getUsername());

        // ==========================================
        // WEB → HttpOnly Cookie
        // ==========================================

        if ("WEB".equalsIgnoreCase(clientType)) {

            ResponseCookie cookie = ResponseCookie
                    .from("access_token", result.getAccessToken())
                    .httpOnly(true)
                    .secure(false)
                    .sameSite("Lax")
                    .path("/")
                    .maxAge(Duration.ofDays(1))
                    .build();

            response.addHeader(
                    HttpHeaders.SET_COOKIE,
                    cookie.toString()
            );
        }

        // ==========================================
        // MOBILE → accessToken
        // ==========================================

        if ("MOBILE".equalsIgnoreCase(clientType)) {
            builder.accessToken(result.getAccessToken());
        }

        return ApiResponse.success(
                "Đăng nhập thành công",
                builder.build()
        );
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserResponse> register(
            @Valid @RequestBody UserCreateRequest request
    ) {
        UserResponse response = authService.register(request);

        return ApiResponse.success(
                "Đăng ký tài khoản thành công",
                response
        );
    }
}