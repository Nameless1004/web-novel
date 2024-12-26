package com.webnovel.auth.dto;

import lombok.Data;

@Data
public class SignupRequestDto {

    private String username;
    private String name;
    private String nickname;
    private String email;
    private String password;
    private String role;
}
