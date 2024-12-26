package com.webnovel.user.service;

import com.webnovel.common.dto.ResponseDto;
import com.webnovel.security.jwt.AuthUser;
import com.webnovel.user.dto.NicknameUpdateRequestDto;

public interface UserService {
    ResponseDto<Void> changeNickname(AuthUser authUser, NicknameUpdateRequestDto request);
}
