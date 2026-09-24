package com.socialnetwork.module.user.controller;

import com.socialnetwork.common.exception.BusinessException;
import com.socialnetwork.common.exception.ErrorCode;
import com.socialnetwork.common.response.ApiResponse;
import com.socialnetwork.common.security.CustomUserDetails;
import com.socialnetwork.module.user.dto.request.UserProfileUpdateRequest;
import com.socialnetwork.module.user.dto.response.UserResponse;
import com.socialnetwork.module.user.dto.response.UserSearchResponse;
import com.socialnetwork.module.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @Operation(summary = "Tìm kiếm người dùng")
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Page<UserSearchResponse>> searchUsers(
            @AuthenticationPrincipal CustomUserDetails userDetails,

            @RequestParam(defaultValue = "") String q,

            @ParameterObject
            @PageableDefault(
                    size = 10,
                    sort = "username",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable
    ) {
        if (userDetails == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        UUID currentUserId =
                userDetails.getUser().getId();

        Page<UserSearchResponse> response =
                userService.searchUsers(
                        currentUserId,
                        q,
                        pageable
                );

        return ApiResponse.success(
                "Tìm kiếm người dùng thành công",
                response
        );
    }

    @Operation(summary = "Cập nhật thông tin cá nhân")
    @PutMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<UserResponse> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UserProfileUpdateRequest request
    ) {
        if (userDetails == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        UUID currentUserId = userDetails.getUser().getId();

        UserResponse response =
                userService.updateProfile(
                        currentUserId,
                        request
                );

        return ApiResponse.success(
                "Cập nhật thông tin cá nhân thành công",
                response
        );
    }

    @Operation(summary = "Lấy thông tin profile của người dùng khác")
    @GetMapping("/{userId}/profile")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<UserResponse> getUserProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID userId
    ) {
        if (userDetails == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        UserResponse response =
                userService.getCurrentUserProfile(userId);

        return ApiResponse.success(
                "Lấy thông tin người dùng thành công",
                response
        );
    }
}