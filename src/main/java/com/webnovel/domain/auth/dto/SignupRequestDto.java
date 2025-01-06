package com.webnovel.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequestDto {

    @NotBlank(message = "사용자 아이디는 필수 입력값입니다")
    @Size(min = 4, max = 20, message = "사용자 아이디는 4자 이상 20자 이하로 입력해주세요")
    private String username;

    @NotBlank(message = "이름은 필수 입력값입니다")
    private String name;

    @NotBlank(message = "닉네임은 필수 입력값입니다")
    @Size(min = 2, max = 30, message = "닉네임은 2자 이상 30자 이하로 입력해주세요")
    private String nickname;

    @NotBlank(message = "이메일은 필수 입력값입니다")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$", message = "비밀번호는 영문, 숫자를 포함한 8자 이상이어야 합니다.")
    private String password;
}
