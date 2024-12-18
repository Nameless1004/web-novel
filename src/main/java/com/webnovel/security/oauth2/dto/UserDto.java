package com.webnovel.security.oauth2.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private String username;
    private String name;
    private String role;

    public UserDto(String username, String role) {
        this.username = username;
        this.role = role;
    }
}
