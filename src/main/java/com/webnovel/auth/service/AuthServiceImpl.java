package com.webnovel.auth.service;

import com.webnovel.auth.dto.LoginRequestDto;
import com.webnovel.auth.dto.LoginResponseDto;
import com.webnovel.auth.dto.SignupRequestDto;
import com.webnovel.auth.dto.SignupResponseDto;
import com.webnovel.common.exceptions.NotFoundException;
import com.webnovel.security.jwt.JwtUtil;
import com.webnovel.security.jwt.TokenType;
import com.webnovel.user.entity.User;
import com.webnovel.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {


    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequest, HttpServletResponse response) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if(!loginRequest.getPassword().equals(user.getPassword())) {
            // TODO 알맞은걸로 수정
            throw new AccessDeniedException("Wrong password");
        }

        String accessToken = jwtUtil.generateJwt(user.getUsername(), user.getRole(), TokenType.ACCESS);
        String refreshToken = jwtUtil.generateJwt(user.getUsername(), user.getRole(), TokenType.REFRESH);
        jwtUtil.addAccessTokenToHeader(accessToken, response);
        jwtUtil.addRefreshTokenToCookie(accessToken, response);
        LoginResponseDto result = new LoginResponseDto(accessToken, accessToken);
        return result;
    }

    @Override
    public SignupResponseDto signup(SignupRequestDto signupRequest) {
        User user = User.builder()
                .username(signupRequest.getUsername())
                .name(signupRequest.getName())
                .password(signupRequest.getPassword())
                .role("ROLE_USER")
                .build();
        User save = userRepository.save(user);

        return new SignupResponseDto(save.getId());
    }
}
