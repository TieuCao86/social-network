package com.socialnetwork.common.exception;

import com.socialnetwork.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // =======================================================
    // Xử lý lỗi nghiệp vụ
    // =======================================================

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException exception,
            HttpServletRequest request
    ) {
        ErrorCode errorCode = exception.getErrorCode();

        log.warn(
                "Lỗi nghiệp vụ | phương thức={} | đường dẫn={} | mã lỗi={} | thông báo={}",
                request.getMethod(),
                request.getRequestURI(),
                errorCode.getCode(),
                errorCode.getMessage()
        );

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.error(errorCode));
    }

    // =======================================================
    // Xử lý lỗi validation với @Valid
    // =======================================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        log.warn(
                "Dữ liệu không hợp lệ | phương thức={} | đường dẫn={} | lỗi={}",
                request.getMethod(),
                request.getRequestURI(),
                errors
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        ErrorCode.VALIDATION_ERROR,
                        errors
                ));
    }

    // =======================================================
    // Xử lý lỗi validation với @Validated
    // =======================================================

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolationException(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        Map<String, String> errors = new LinkedHashMap<>();

        exception.getConstraintViolations()
                .forEach(violation ->
                        errors.put(
                                violation.getPropertyPath().toString(),
                                violation.getMessage()
                        )
                );

        log.warn(
                "Vi phạm điều kiện dữ liệu | phương thức={} | đường dẫn={} | lỗi={}",
                request.getMethod(),
                request.getRequestURI(),
                errors
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        ErrorCode.VALIDATION_ERROR,
                        errors
                ));
    }

    // =======================================================
    // Xử lý request body không hợp lệ
    // =======================================================

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        log.warn(
                "Request body không hợp lệ | phương thức={} | đường dẫn={}",
                request.getMethod(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        ErrorCode.INVALID_REQUEST
                ));
    }

    // =======================================================
    // Xử lý thiếu request parameter
    // =======================================================

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException exception,
            HttpServletRequest request
    ) {
        log.warn(
                "Thiếu request parameter | phương thức={} | đường dẫn={} | parameter={}",
                request.getMethod(),
                request.getRequestURI(),
                exception.getParameterName()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        ErrorCode.INVALID_REQUEST
                ));
    }

    // =======================================================
    // Xử lý thiếu path variable
    // =======================================================

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingPathVariableException(
            MissingPathVariableException exception,
            HttpServletRequest request
    ) {
        log.warn(
                "Thiếu path variable | phương thức={} | đường dẫn={} | variable={}",
                request.getMethod(),
                request.getRequestURI(),
                exception.getVariableName()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        ErrorCode.INVALID_REQUEST
                ));
    }

    // =======================================================
    // Xử lý lỗi xác thực
    // =======================================================

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(
            AuthenticationException exception,
            HttpServletRequest request
    ) {
        log.warn(
                "Xác thực thất bại | phương thức={} | đường dẫn={}",
                request.getMethod(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(
                        ErrorCode.UNAUTHORIZED
                ));
    }

    // =======================================================
    // Xử lý lỗi không có quyền
    // =======================================================

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(
            AccessDeniedException exception,
            HttpServletRequest request
    ) {
        log.warn(
                "Truy cập bị từ chối | phương thức={} | đường dẫn={}",
                request.getMethod(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(
                        ErrorCode.FORBIDDEN
                ));
    }

    // =======================================================
    // Xử lý phương thức HTTP không được hỗ trợ
    // =======================================================

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException exception,
            HttpServletRequest request
    ) {
        log.warn(
                "Phương thức HTTP không được hỗ trợ | phương thức={} | đường dẫn={}",
                request.getMethod(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error(
                        ErrorCode.METHOD_NOT_ALLOWED
                ));
    }

    // =======================================================
    // Xử lý resource không tồn tại
    // =======================================================

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFoundException(
            NoResourceFoundException exception,
            HttpServletRequest request
    ) {
        log.debug(
                "Không tìm thấy resource | phương thức={} | đường dẫn={}",
                request.getMethod(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(
                        ErrorCode.RESOURCE_NOT_FOUND
                ));
    }

    // =======================================================
    // Xử lý tham số không hợp lệ
    // =======================================================

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
            IllegalArgumentException exception,
            HttpServletRequest request
    ) {
        log.warn(
                "Tham số không hợp lệ | phương thức={} | đường dẫn={} | thông báo={}",
                request.getMethod(),
                request.getRequestURI(),
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        ErrorCode.INVALID_REQUEST
                ));
    }

    // =======================================================
    // Xử lý vi phạm ràng buộc dữ liệu Database (UNIQUE, Foreign Key...)
    // =======================================================

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(
            org.springframework.dao.DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {
        // Lấy nguyên nhân gốc từ Database Driver (PostgreSQL/MySQL/H2)
        String rootCauseMessage = exception.getRootCause() != null
                ? exception.getRootCause().getMessage().toLowerCase()
                : "";

        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR; // Default fallback

        // 1. Phân tích tên CONSTRAINT đặt ở DDL
        if (rootCauseMessage.contains("uk_users_username") || rootCauseMessage.contains("username")) {
            errorCode = ErrorCode.USERNAME_ALREADY_EXISTS;
        } else if (rootCauseMessage.contains("uk_users_email") || rootCauseMessage.contains("email")) {
            errorCode = ErrorCode.EMAIL_ALREADY_EXISTS;
        } else if (rootCauseMessage.contains("uk_users_phone") || rootCauseMessage.contains("phone")) {
            errorCode = ErrorCode.PHONE_ALREADY_EXISTS;
        }

        log.warn(
                "Vi phạm ràng buộc UNIQUE Database | phương thức={} | đường dẫn={} | mã lỗi={} | nguyên nhân={}",
                request.getMethod(),
                request.getRequestURI(),
                errorCode.getCode(),
                rootCauseMessage
        );

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.error(errorCode));
    }

    // =======================================================
    // Xử lý tất cả lỗi không xác định
    // =======================================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(
            Exception exception,
            HttpServletRequest request
    ) {
        log.error(
                "Lỗi hệ thống không xác định | phương thức={} | đường dẫn={}",
                request.getMethod(),
                request.getRequestURI(),
                exception
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        ErrorCode.INTERNAL_SERVER_ERROR
                ));
    }
}