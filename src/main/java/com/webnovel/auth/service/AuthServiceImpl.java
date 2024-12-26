package com.webnovel.auth.service;

import com.webnovel.auth.dto.LoginRequestDto;
import com.webnovel.auth.dto.LoginResponseDto;
import com.webnovel.auth.dto.SignupRequestDto;
import com.webnovel.auth.dto.SignupResponseDto;
import com.webnovel.common.exceptions.DuplicatedException;
import com.webnovel.common.exceptions.InvalidRequestException;
import com.webnovel.common.exceptions.NotFoundException;
import com.webnovel.security.jwt.JwtUtil;
import com.webnovel.security.jwt.TokenType;
import com.webnovel.user.entity.User;
import com.webnovel.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequest, HttpServletResponse response) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidRequestException("Wrong password");
        }

        String accessToken = jwtUtil.generateJwt(user.getUsername(), user.getRole(), TokenType.ACCESS);
        String refreshToken = jwtUtil.generateJwt(user.getUsername(), user.getRole(), TokenType.REFRESH);

        jwtUtil.addAccessTokenToHeader(accessToken, response);
        jwtUtil.addRefreshTokenToCookie(refreshToken, response);

        return new LoginResponseDto(accessToken, accessToken);
    }

    @Override
    public SignupResponseDto signup(SignupRequestDto signupRequest) {

        // 닉네임 중복 검사
        if(userRepository.existsByNickname(signupRequest.getNickname())) {
            throw new DuplicatedException("Nickname already exists");
        }

        // 이메일 중복 검사
        if(userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new DuplicatedException("Email already exists");
        }

        String password = passwordEncoder.encode(signupRequest.getPassword());

        User user = User.builder()
                .username(signupRequest.getUsername())
                .name(signupRequest.getName())
                .nickname(signupRequest.getNickname())
                .email(signupRequest.getEmail())
                .password(password)
                .role("ROLE_USER")
                .build();
        User save = userRepository.save(user);

        return new SignupResponseDto(save.getId());
    }
}
