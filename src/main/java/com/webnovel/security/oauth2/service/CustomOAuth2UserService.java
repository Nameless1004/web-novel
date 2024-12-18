package com.webnovel.security.oauth2.service;

import com.webnovel.oauth2.dto.*;
import com.webnovel.security.oauth2.dto.*;
import com.webnovel.user.entity.User;
import com.webnovel.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("loadUser : {}", oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = null;

        if(registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else if(registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else{
            return null;
        }

        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        User existsUser = userRepository.findByUsername(username).orElse(null);

        // 등록이 안돼있는 유저
        if(existsUser == null) {

            User newUser = User.builder()
                    .username(username)
                    .email(oAuth2Response.getEmail())
                    .name(oAuth2Response.getName())
                    .role("ROLE_USER")
                    .password("")
                    .build();

            userRepository.save(newUser);

            UserDto userDto = UserDto.builder()
                    .username(username)
                    .name(oAuth2Response.getName())
                    .role("ROLE_USER")
                    .build();

            return new CustomOAuth2User(userDto);
        }
        else {

            existsUser.update(oAuth2Response.getName(), oAuth2Response.getEmail());
            existsUser = userRepository.save(existsUser);

            UserDto userDto = UserDto.builder()
                    .username(existsUser.getUsername())
                    .name(existsUser.getName())
                    .role(existsUser.getRole())
                    .build();

            return new CustomOAuth2User(userDto);
        }
    }
}
