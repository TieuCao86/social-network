package com.socialnetwork.module.user.controller;

import com.socialnetwork.common.response.ApiResponse;
import com.socialnetwork.common.security.CustomUserDetails;
import com.socialnetwork.module.user.dto.response.UserResponse;
import com.socialnetwork.module.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Domain", description = "APIs quản lý thông tin người dùng và Profile")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Lấy thông tin cá nhân của người dùng đang đăng nhập")
    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<UserResponse> getProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // Lấy UUID từ Principal thay vì truyền cả Entity User
        UUID currentUserId = userDetails.getUser().getId();

        UserResponse response = userService.getCurrentUserProfile(currentUserId);

        return ApiResponse.success(
                "Lấy thông tin người dùng thành công",
                response
        );
    }
}