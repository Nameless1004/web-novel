package com.webnovel.domain.security.oauth2.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OAuth2Provider {
    GOOGLE("google"), NAVER("naver"), KAKAO("kakao"), GITHUB("github");

    public static OAuth2Provider parse(String registrationId) {
        OAuth2Provider[] values = values();
        for (OAuth2Provider value : values) {
            if(value.getRegistrationId().equals(registrationId)) {
                return value;
            }
        }

        return null;
    }
    private final String registrationId;
}
