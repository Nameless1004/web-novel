package com.webnovel.domain.security.oauth2.dto;

import java.util.Map;

public class KakaoResponse implements OAuth2Response{

    private Map<String, Object> attributes;

    public KakaoResponse(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return "id";
    }

    @Override
    public String getEmail() {
        return "email";
    }

    @Override
    public String getName() {
        return "name";
    }
}
