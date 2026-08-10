package com.socialnetwork.module.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateRequest {

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    @Size(max = 255, message = "Email không được vượt quá 255 ký tự")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(
            min = 8,
            max = 100,
            message = "Mật khẩu phải từ 8 đến 100 ký tự"
    )
    private String password;

    @NotBlank(message = "Tên người dùng không được để trống")
    @Size(
            min = 3,
            max = 50,
            message = "Tên người dùng phải từ 3 đến 50 ký tự"
    )
    private String username;
}