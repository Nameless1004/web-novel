package com.webnovel.domain.user.service;

import com.webnovel.common.dto.ResponseDto;
import com.webnovel.domain.security.jwt.AuthUser;
import com.webnovel.domain.user.dto.DuplicatedResponseDto;
import com.webnovel.domain.user.dto.NicknameUpdateRequestDto;
import com.webnovel.domain.user.dto.UserDetailsDto;

public interface UserService {
    ResponseDto<Void> changeNickname(AuthUser authUser, NicknameUpdateRequestDto request);
    ResponseDto<UserDetailsDto> getUserDetails(String nickname);

    ResponseDto<DuplicatedResponseDto> checkNickname(String nickname);
    ResponseDto<DuplicatedResponseDto> checkUsername(String username);
    ResponseDto<DuplicatedResponseDto> checkEmail(String email);
}
