package com.webnovel.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReissueResponseDto {

    private Long userId;
    private String accessToken;
    private String refreshToken;
}
