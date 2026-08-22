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

    USER_NOT_ACTIVE(
            "USER_005",
            "Tài khoản đã dừng hoạt động.",
            HttpStatus.CONFLICT
    ),

    PHONE_ALREADY_EXISTS(
            "USER_006",
            "Số điện thoại đã được sử dụng.",
            HttpStatus.CONFLICT
    ),

    EMAIL_OR_PHONE_REQUIRED(
            "USER_007",
            "Email hoặc số điện thoại phải được cung cấp.",
            HttpStatus.BAD_REQUEST
    ),

    // =======================================================
    // Relationship - Friendship
    // =======================================================

    CANNOT_FRIEND_SELF(
            "RELATIONSHIP_001",
            "Không thể gửi lời mời kết bạn cho chính mình.",
            HttpStatus.BAD_REQUEST
    ),

    FRIEND_REQUEST_ALREADY_SENT(
            "RELATIONSHIP_002",
            "Bạn đã gửi lời mời kết bạn trước đó.",
            HttpStatus.CONFLICT
    ),

    FRIEND_REQUEST_NOT_FOUND(
            "RELATIONSHIP_003",
            "Không tìm thấy lời mời kết bạn.",
            HttpStatus.NOT_FOUND
    ),

    INVALID_FRIEND_REQUEST_STATUS(
            "RELATIONSHIP_004",
            "Lời mời kết bạn không hợp lệ.",
            HttpStatus.BAD_REQUEST
    ),

    ALREADY_FRIENDS(
            "RELATIONSHIP_005",
            "Hai người đã là bạn bè.",
            HttpStatus.CONFLICT
    ),

    FRIENDSHIP_NOT_FOUND(
            "RELATIONSHIP_006",
            "Không tìm thấy quan hệ bạn bè.",
            HttpStatus.NOT_FOUND
    ),

    NOT_FRIENDS(
            "RELATIONSHIP_007",
            "Hai người không phải là bạn bè.",
            HttpStatus.BAD_REQUEST
    ),

    FRIEND_REQUEST_ALREADY_RECEIVED(
            "RELATIONSHIP_010",
            "Người dùng này đã gửi lời mời kết bạn cho bạn.",
            HttpStatus.CONFLICT
    ),

    BLOCKING_USER(
            "RELATIONSHIP_008",
            "Bạn đã chặn người dùng này.",
            HttpStatus.FORBIDDEN
    ),

    BLOCKED_BY_USER(
            "RELATIONSHIP_009",
            "Bạn đã bị người dùng này chặn.",
            HttpStatus.FORBIDDEN
    ),

    ALREADY_FOLLOWING(
            "RELATIONSHIP_001",
            "Bạn đã theo dõi người dùng này.",
            HttpStatus.CONFLICT
    ),

    NOT_FOLLOWING(
            "RELATIONSHIP_002",
            "Bạn chưa theo dõi người dùng này.",
            HttpStatus.BAD_REQUEST
    ),

    CANNOT_FOLLOW_SELF(
            "RELATIONSHIP_003",
            "Không thể theo dõi chính mình.",
            HttpStatus.BAD_REQUEST
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