package com.webnovel.security.oauth2.service;

import com.webnovel.security.oauth2.dto.CustomOAuth2User;
import com.webnovel.security.oauth2.dto.OAuth2Response;
import com.webnovel.security.oauth2.dto.UserDto;
import com.webnovel.user.entity.User;
import com.webnovel.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.UUID;

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

        // naver, google인지
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = OAuth2ResponseFactory.createOAuth2Response(registrationId, oAuth2User.getAttributes());

        // registrationId와 일치하는 제공자가 없으면 예외
        if(oAuth2Response == null) {
            throw new OAuth2AuthenticationException("unkown provider -> "+ registrationId );
        }

        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        User existsUser = userRepository.findByUsername(username).orElse(null);

        // 등록이 안돼있는 유저
        if(existsUser == null) {

            User newUser = User.builder()
                    .username(username)
                    .email(oAuth2Response.getEmail())
                    .nickname(oAuth2Response.getProvider() + "-" + UUID.randomUUID().toString().substring(0, 7))
                    .name(oAuth2Response.getName())
                    .role("ROLE_USER")
                    .password("")
                    .build();

            newUser = userRepository.save(newUser);

            UserDto userDto = UserDto.builder()
                    .username(username)
                    .id(newUser.getId().toString())
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
                    .id(existsUser.getId().toString())
                    .name(existsUser.getName())
                    .role(existsUser.getRole())
                    .build();

            return new CustomOAuth2User(userDto);
        }
    }
}
