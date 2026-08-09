package com.socialnetwork.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // =======================================================
    // Common
    // =======================================================

    INTERNAL_SERVER_ERROR(
            "COMMON_001",
            "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.",
            HttpStatus.INTERNAL_SERVER_ERROR
    ),

    INVALID_REQUEST(
            "COMMON_002",
            "Yêu cầu không hợp lệ.",
            HttpStatus.BAD_REQUEST
    ),

    VALIDATION_ERROR(
            "COMMON_003",
            "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại.",
            HttpStatus.BAD_REQUEST
    ),

    RESOURCE_NOT_FOUND(
            "COMMON_004",
            "Không tìm thấy tài nguyên yêu cầu.",
            HttpStatus.NOT_FOUND
    ),

    UNAUTHORIZED(
            "COMMON_005",
            "Bạn chưa đăng nhập hoặc phiên đăng nhập đã hết hạn.",
            HttpStatus.UNAUTHORIZED
    ),

    FORBIDDEN(
            "COMMON_006",
            "Bạn không có quyền thực hiện thao tác này.",
            HttpStatus.FORBIDDEN
    ),

    METHOD_NOT_ALLOWED(
            "COMMON_007",
            "Phương thức yêu cầu không được hỗ trợ.",
            HttpStatus.METHOD_NOT_ALLOWED
    ),

    TOO_MANY_REQUESTS(
            "COMMON_008",
            "Bạn đã thực hiện quá nhiều yêu cầu. Vui lòng thử lại sau.",
            HttpStatus.TOO_MANY_REQUESTS
    ),

    // =======================================================
    // User
    // =======================================================

    USER_NOT_FOUND(
            "USER_001",
            "Không tìm thấy người dùng.",
            HttpStatus.NOT_FOUND
    ),

    USER_ALREADY_EXISTS(
            "USER_002",
            "Người dùng đã tồn tại.",
            HttpStatus.CONFLICT
    ),

    EMAIL_ALREADY_EXISTS(
            "USER_003",
            "Email đã được sử dụng.",
            HttpStatus.CONFLICT
    ),

    USERNAME_ALREADY_EXISTS(
            "USER_004",
            "Tên người dùng đã được sử dụng.",
            HttpStatus.CONFLICT
    ),

    // =======================================================
    // Authentication
    // =======================================================

    INVALID_CREDENTIALS(
            "AUTH_001",
            "Email hoặc mật khẩu không chính xác.",
            HttpStatus.UNAUTHORIZED
    ),

    INVALID_TOKEN(
            "AUTH_002",
            "Token không hợp lệ.",
            HttpStatus.UNAUTHORIZED
    ),

    TOKEN_EXPIRED(
            "AUTH_003",
            "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.",
            HttpStatus.UNAUTHORIZED
    ),

    REFRESH_TOKEN_INVALID(
            "AUTH_004",
            "Refresh token không hợp lệ hoặc đã hết hạn.",
            HttpStatus.UNAUTHORIZED
    ),

    REFRESH_TOKEN_REVOKED(
            "AUTH_005",
            "Refresh token đã bị vô hiệu hóa.",
            HttpStatus.UNAUTHORIZED
    ),

    // =======================================================
    // Password
    // =======================================================

    PASSWORD_INCORRECT(
            "PASSWORD_001",
            "Mật khẩu hiện tại không chính xác.",
            HttpStatus.BAD_REQUEST
    ),

    PASSWORD_CONFIRMATION_FAILED(
            "PASSWORD_002",
            "Mật khẩu xác nhận không khớp.",
            HttpStatus.BAD_REQUEST
    ),

    PASSWORD_TOO_WEAK(
            "PASSWORD_003",
            "Mật khẩu không đủ mạnh.",
            HttpStatus.BAD_REQUEST
    );

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}