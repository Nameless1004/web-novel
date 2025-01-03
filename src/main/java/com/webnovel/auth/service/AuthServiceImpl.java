package com.webnovel.auth.service;

import com.webnovel.auth.dto.*;
import com.webnovel.common.dto.ResponseDto;
import com.webnovel.common.exceptions.DuplicatedException;
import com.webnovel.common.exceptions.InvalidRequestException;
import com.webnovel.common.exceptions.NotFoundException;
import com.webnovel.security.jwt.AuthUser;
import com.webnovel.security.jwt.JwtUtil;
import com.webnovel.security.jwt.TokenType;
import com.webnovel.user.entity.User;
import com.webnovel.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

        String accessToken = jwtUtil.generateJwt(user.getId().toString(), user.getUsername(), user.getRole(), TokenType.ACCESS);
        String refreshToken = jwtUtil.generateJwt(user.getId().toString(), user.getUsername(), user.getRole(), TokenType.REFRESH);

        jwtUtil.addAccessTokenToHeader(accessToken, response);
        jwtUtil.addRefreshTokenToCookie(refreshToken, response);

        return new LoginResponseDto(user.getId(), accessToken, refreshToken);
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

    @Override
    public ResponseDto<ReissueResponseDto> reissue(ReissueRequestDto request) {
        String token = request.getRefreshToken().substring(7);
        if(!jwtUtil.validateToken(token)) {
            throw new InvalidRequestException("Invalid refresh token");
        }

        if (!jwtUtil.getType(token).equals(TokenType.REFRESH.name())) {

            throw new InvalidRequestException("Invalid refresh token");
        }

        String id = jwtUtil.getId(token);
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        var access = jwtUtil.generateJwt(id, username, role, TokenType.ACCESS);
        var refresh = jwtUtil.generateJwt(id, username, role, TokenType.REFRESH);

        return ResponseDto.of(HttpStatus.OK ,new ReissueResponseDto(Long.parseLong(id), access, refresh));
    }
}
