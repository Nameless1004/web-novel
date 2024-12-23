package com.webnovel.auth.service;

import com.webnovel.auth.dto.LoginRequestDto;
import com.webnovel.auth.dto.LoginResponseDto;
import com.webnovel.auth.dto.SignupRequestDto;
import com.webnovel.auth.dto.SignupResponseDto;
import com.webnovel.security.dto.LoginRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    LoginResponseDto login(LoginRequestDto loginRequest, HttpServletResponse response);
    SignupResponseDto signup(SignupRequestDto signupRequest);

}
