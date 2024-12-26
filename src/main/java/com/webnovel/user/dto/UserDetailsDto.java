package com.webnovel.user.dto;

import com.webnovel.user.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDetailsDto {

    private String username;
    private String nickname;

    public UserDetailsDto(User user) {
        this.username = user.getUsername();
        this.nickname = user.getNickname();
    }
}
