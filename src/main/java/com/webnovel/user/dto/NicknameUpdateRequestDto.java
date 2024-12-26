package com.webnovel.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NicknameUpdateRequestDto {
    @NotBlank(message = "닉네임은 필수 입력값입니다")
    private String nickname;
}
