package com.socialnetwork.module.user.controller;

import com.socialnetwork.common.response.ApiResponse;
import com.socialnetwork.common.security.CustomUserDetails;
import com.socialnetwork.module.user.dto.response.UserResponse;
import com.socialnetwork.module.user.entity.User;
import com.socialnetwork.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ApiResponse<UserResponse> getProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        User user = userDetails.getUser();

        UserResponse response = userService.getCurrentUserProfile(user);

        return ApiResponse.success(
                "Lấy thông tin người dùng thành công",
                response
        );
    }
}