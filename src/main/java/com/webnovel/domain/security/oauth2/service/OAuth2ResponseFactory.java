package com.webnovel.domain.security.oauth2.service;

import com.webnovel.domain.security.oauth2.dto.GoogleResponse;
import com.webnovel.domain.security.oauth2.dto.KakaoResponse;
import com.webnovel.domain.security.oauth2.dto.NaverResponse;
import com.webnovel.domain.security.oauth2.dto.OAuth2Response;
import com.webnovel.domain.security.oauth2.enums.OAuth2Provider;

import java.util.Map;

public class OAuth2ResponseFactory {

    /**
     * 해당 registrationId에 맞는 OAuth2Response를 생성합니다. 일치하는 제공자가 없을 시 null 반환
     * @param registrationId
     * @param attributes
     * @return
     */
    public static OAuth2Response createOAuth2Response(String registrationId, Map<String, Object> attributes) {
        OAuth2Provider provider = OAuth2Provider.parse(registrationId);

        return switch (provider) {
            case GOOGLE -> new GoogleResponse(attributes);
            case NAVER -> new NaverResponse(attributes);
            case KAKAO -> new KakaoResponse(attributes);

            default -> null;
        };
    }
}
