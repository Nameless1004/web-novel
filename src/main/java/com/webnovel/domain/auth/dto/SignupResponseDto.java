package com.webnovel.domain.auth.dto;

import lombok.Data;

@Data
public class SignupResponseDto {
    private Long id;

    public SignupResponseDto(Long id) {
        this.id = id;
    }
}
