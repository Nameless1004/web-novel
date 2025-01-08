package com.webnovel.domain.auth.service;

import com.webnovel.common.dto.ResponseDto;
import com.webnovel.domain.auth.dto.*;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    LoginResponseDto login(LoginRequestDto loginRequest, HttpServletResponse response);
    SignupResponseDto signup(SignupRequestDto signupRequest);

    ResponseDto<ReissueResponseDto> reissue(ReissueRequestDto request);
}
