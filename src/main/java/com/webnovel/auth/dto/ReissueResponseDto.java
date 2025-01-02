package com.webnovel.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReissueResponseDto {

    private String accessToken;
    private String refreshToken;
}
