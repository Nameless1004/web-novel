package com.webnovel.auth.controller;

import com.webnovel.auth.dto.*;
import com.webnovel.auth.service.AuthServiceImpl;
import com.webnovel.common.dto.ResponseDto;
import com.webnovel.security.jwt.AuthUser;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthServiceImpl authService;

    @PostMapping("/auth/login")
    public ResponseEntity<ResponseDto<LoginResponseDto>> login(
            @RequestBody LoginRequestDto loginRequestDto,
            HttpServletResponse response
    ) {
        LoginResponseDto login = authService.login(loginRequestDto, response);

        return ResponseDto.of(HttpStatus.OK, login).toEntity();
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<ResponseDto<SignupResponseDto>> signup(
            @Validated @RequestBody SignupRequestDto signupRequestDto
    ) {
        SignupResponseDto signup = authService.signup(signupRequestDto);

        return ResponseDto.of(HttpStatus.OK, signup).toEntity();
    }

    @PostMapping("/auth/reissue")
    public ResponseEntity<ResponseDto<ReissueResponseDto>> reissue(
            @RequestBody ReissueRequestDto request) {
        return authService.reissue(request).toEntity();
    }
}
