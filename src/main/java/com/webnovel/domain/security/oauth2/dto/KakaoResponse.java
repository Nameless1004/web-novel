package com.webnovel.domain.security.oauth2.dto;

import java.util.Map;

public class KakaoResponse implements OAuth2Response{

    private Map<String, Object> attributes;

    public KakaoResponse(Map<String, Object> attributes) {
        this.attributes = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }

    @Override
    public String getName() {
        return attributes.get("name").toString();
    }
}
