package com.webnovel.user.service;

import com.webnovel.common.dto.ResponseDto;
import com.webnovel.common.exceptions.DuplicatedException;
import com.webnovel.security.jwt.AuthUser;
import com.webnovel.user.dto.NicknameUpdateRequestDto;
import com.webnovel.user.entity.User;
import com.webnovel.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public ResponseDto<Void> changeNickname(AuthUser authUser, NicknameUpdateRequestDto request) {
        if(userRepository.existsByNickname(request.getNickname())) {
            throw new DuplicatedException("중복된 닉네임입니다.");
        }

        User user = userRepository.findByUsernameOrElseThrow(authUser.getUsername());
        user.updateNickname(request.getNickname());
        userRepository.save(user);
        return ResponseDto.of(HttpStatus.OK, "성공적으로 닉네임이 변경됐습니다.");
    }
}
