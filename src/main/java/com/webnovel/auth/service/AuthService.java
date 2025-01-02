package com.webnovel.auth.service;

import com.webnovel.auth.dto.*;
import com.webnovel.common.dto.ResponseDto;
import com.webnovel.security.jwt.AuthUser;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    LoginResponseDto login(LoginRequestDto loginRequest, HttpServletResponse response);
    SignupResponseDto signup(SignupRequestDto signupRequest);

    ResponseDto<ReissueResponseDto> reissue(ReissueRequestDto request);
}
