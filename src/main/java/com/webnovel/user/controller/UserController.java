package com.webnovel.user.controller;

import com.webnovel.common.dto.ResponseDto;
import com.webnovel.security.jwt.AuthUser;
import com.webnovel.user.dto.DuplicatedResponseDto;
import com.webnovel.user.dto.NicknameUpdateRequestDto;
import com.webnovel.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PatchMapping("/nickname")
    public ResponseEntity<ResponseDto<Void>> changeNickname(
            @AuthenticationPrincipal AuthUser authUser,
            @Validated @RequestBody NicknameUpdateRequestDto request) {
        return userService.changeNickname(authUser, request)
                .toEntity();
    }

    @GetMapping("/check/nickname")
    public ResponseEntity<ResponseDto<DuplicatedResponseDto>> checkNickname(@RequestParam String nickname) {
        return userService.checkNickname(nickname).toEntity();
    }

    @GetMapping("/check/username")
    public ResponseEntity<ResponseDto<DuplicatedResponseDto>> checkUsername(@RequestParam String username) {
        return userService.checkUsername(username).toEntity();
    }

    @GetMapping("/check/email")
    public ResponseEntity<ResponseDto<DuplicatedResponseDto>> checkEmail(@RequestParam String email) {
        return userService.checkEmail(email).toEntity();
    }
}
