package com.webnovel.user.service;

import com.webnovel.common.dto.ResponseDto;
import com.webnovel.security.jwt.AuthUser;
import com.webnovel.user.dto.NicknameUpdateRequestDto;
import com.webnovel.user.dto.UserDetailsDto;

public interface UserService {
    ResponseDto<Void> changeNickname(AuthUser authUser, NicknameUpdateRequestDto request);
    ResponseDto<UserDetailsDto> getUserDetails(String nickname);
}
