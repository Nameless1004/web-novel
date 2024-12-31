package com.webnovel.security.jwt;

import com.webnovel.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Getter
public class AuthUser {

    private Long id;
    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;
    private final String role;

    public AuthUser(String id, String username, String role) {
        this.id = Long.parseLong(id);
        this.username = username;
        this.authorities = List.of(new SimpleGrantedAuthority(role));
        this.role = role;
    }

    public User toUserEntity() {
        return new User(id, username, role);
    }
}
