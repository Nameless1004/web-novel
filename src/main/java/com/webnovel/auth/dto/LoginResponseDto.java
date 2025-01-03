package com.webnovel.auth.dto;

import lombok.Data;

@Data
public class LoginResponseDto {
    private Long userId;
    private String accessToken;
    private String refreshToken;

    public LoginResponseDto(Long userId, String accessToken, String refreshToken) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
