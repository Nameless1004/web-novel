package com.webnovel.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenType {
    ACCESS(60 * 1000L),
    REFRESH(24 * 60 * 60 * 1000L),;

    private final long LIFETIME_MS;
}
