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

    private String id;
    private String username;
    private String name;
    private String role;

    public UserDto(String id, String username, String role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }
}
