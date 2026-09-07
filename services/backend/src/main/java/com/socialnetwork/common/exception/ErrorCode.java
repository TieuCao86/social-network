package com.socialnetwork.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // =======================================================
    // 1. Common / General Errors (COMMON_xxx)
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
    // 2. Authentication (AUTH_xxx)
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
    // 3. Password (PASSWORD_xxx)
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
    ),

    // =======================================================
    // 4. User (USER_xxx)
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
    // 5. Relationship & Social (RELATIONSHIP_xxx)
    // =======================================================

    CANNOT_VIEW_OWN_RELATIONSHIP(
            "RELATIONSHIP_001",
            "Không thể xem trạng thái quan hệ với chính mình.",
            HttpStatus.BAD_REQUEST
    ),

    CANNOT_FRIEND_SELF(
            "RELATIONSHIP_002",
            "Không thể gửi lời mời kết bạn cho chính mình.",
            HttpStatus.BAD_REQUEST
    ),

    FRIEND_REQUEST_ALREADY_SENT(
            "RELATIONSHIP_003",
            "Bạn đã gửi lời mời kết bạn trước đó.",
            HttpStatus.CONFLICT
    ),

    FRIEND_REQUEST_ALREADY_RECEIVED(
            "RELATIONSHIP_004",
            "Người dùng này đã gửi lời mời kết bạn cho bạn.",
            HttpStatus.CONFLICT
    ),

    FRIEND_REQUEST_NOT_FOUND(
            "RELATIONSHIP_005",
            "Không tìm thấy lời mời kết bạn.",
            HttpStatus.NOT_FOUND
    ),

    INVALID_FRIEND_REQUEST_STATUS(
            "RELATIONSHIP_006",
            "Lời mời kết bạn không hợp lệ.",
            HttpStatus.BAD_REQUEST
    ),

    ALREADY_FRIENDS(
            "RELATIONSHIP_007",
            "Hai người đã là bạn bè.",
            HttpStatus.CONFLICT
    ),

    FRIENDSHIP_NOT_FOUND(
            "RELATIONSHIP_008",
            "Không tìm thấy quan hệ bạn bè.",
            HttpStatus.NOT_FOUND
    ),

    NOT_FRIENDS(
            "RELATIONSHIP_009",
            "Hai người không phải là bạn bè.",
            HttpStatus.BAD_REQUEST
    ),

    BLOCKING_USER(
            "RELATIONSHIP_010",
            "Bạn đã chặn người dùng này.",
            HttpStatus.FORBIDDEN
    ),

    BLOCKED_BY_USER(
            "RELATIONSHIP_011",
            "Bạn đã bị người dùng này chặn.",
            HttpStatus.FORBIDDEN
    ),

    CANNOT_FOLLOW_SELF(
            "RELATIONSHIP_012",
            "Không thể theo dõi chính mình.",
            HttpStatus.BAD_REQUEST
    ),

    ALREADY_FOLLOWING(
            "RELATIONSHIP_013",
            "Bạn đã theo dõi người dùng này.",
            HttpStatus.CONFLICT
    ),

    NOT_FOLLOWING(
            "RELATIONSHIP_014",
            "Bạn chưa theo dõi người dùng này.",
            HttpStatus.BAD_REQUEST
    ),

    // =======================================================
// 6. Comment (COMMENT_xxx)
// =======================================================

    COMMENT_NOT_FOUND(
            "COMMENT_001",
            "Không tìm thấy bình luận.",
            HttpStatus.NOT_FOUND
    ),

    COMMENT_NOT_OWNER(
            "COMMENT_002",
            "Bạn không có quyền thực hiện thao tác với bình luận này.",
            HttpStatus.FORBIDDEN
    ),

    COMMENT_DELETED(
            "COMMENT_003",
            "Bình luận đã bị xóa.",
            HttpStatus.BAD_REQUEST
    ),

    PARENT_COMMENT_NOT_FOUND(
            "COMMENT_004",
            "Không tìm thấy bình luận cha.",
            HttpStatus.NOT_FOUND
    ),

    PARENT_COMMENT_DELETED(
            "COMMENT_005",
            "Bình luận cha đã bị xóa.",
            HttpStatus.BAD_REQUEST
    ),

    COMMENT_CONTENT_REQUIRED(
            "COMMENT_006",
            "Bình luận phải có nội dung hoặc ít nhất một tệp media.",
            HttpStatus.BAD_REQUEST
    ),

    COMMENT_MEDIA_INVALID(
            "COMMENT_007",
            "Media của bình luận không hợp lệ.",
            HttpStatus.BAD_REQUEST
    ),

    COMMENT_MEDIA_NOT_FOUND(
            "COMMENT_008",
            "Không tìm thấy media của bình luận.",
            HttpStatus.NOT_FOUND
    );

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}