package com.webnovel.domain.user.service;

import com.webnovel.common.dto.ResponseDto;
import com.webnovel.common.exceptions.DuplicatedException;
import com.webnovel.common.exceptions.NotFoundException;
import com.webnovel.domain.security.jwt.AuthUser;
import com.webnovel.domain.user.dto.DuplicatedResponseDto;
import com.webnovel.domain.user.dto.NicknameUpdateRequestDto;
import com.webnovel.domain.user.dto.UserDetailsDto;
import com.webnovel.domain.user.dto.UserProfileResponseDto;
import com.webnovel.domain.user.entity.User;
import com.webnovel.domain.user.repository.UserRepository;
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

    @Override
    public ResponseDto<UserDetailsDto> getUserDetails(String nickname) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(()-> new NotFoundException("User not found."));

        return ResponseDto.of(HttpStatus.OK, new UserDetailsDto(user));
    }

    @Override
    public ResponseDto<DuplicatedResponseDto> checkNickname(String nickname) {
        return ResponseDto.of(HttpStatus.OK, new DuplicatedResponseDto(userRepository.existsByNickname(nickname)));
    }

    @Override
    public ResponseDto<DuplicatedResponseDto> checkUsername(String username) {
        return ResponseDto.of(HttpStatus.OK, new DuplicatedResponseDto(userRepository.existsByUsername(username)));
    }

    @Override
    public ResponseDto<DuplicatedResponseDto> checkEmail(String email) {
        return ResponseDto.of(HttpStatus.OK, new DuplicatedResponseDto(userRepository.existsByEmail(email)));
    }

    @Override
    public ResponseDto<UserProfileResponseDto> getProfile(AuthUser authUser) {
        User user = userRepository.findByIdOrElseThrow(authUser.getId());
        UserProfileResponseDto response = UserProfileResponseDto.builder()
                .nickname(user.getNickname())
                .build();

        return ResponseDto.of(HttpStatus.OK, response);
    }
}
