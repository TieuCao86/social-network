package com.socialnetwork.module.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "Số điện thoại hoặc Email không được để trống")
    private String phoneOrEmail;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;
}