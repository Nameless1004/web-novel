package com.webnovel.domain.security.oauth2.dto;

import java.util.Map;

public class KakaoResponse implements OAuth2Response{

    String id;
    private Map<String, Object> account;
    private Map<String, Object> profile;

    public KakaoResponse(Map<String, Object> attributes) {
        id = String.valueOf(attributes.get("id"));
        this.account = (Map<String, Object>) attributes.get("kakao_account");
        this.profile = (Map<String, Object>) account.get("profile");
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return id;
    }

    @Override
    public String getEmail() {
        return account.get("email").toString();
    }

    @Override
    public String getName() {
        return profile.get("nickname").toString();
    }
}
