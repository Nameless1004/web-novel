package com.webnovel.novel.controller;

import com.webnovel.common.dto.ResponseDto;
import com.webnovel.novel.dto.NovelCreateRequestDto;
import com.webnovel.novel.dto.NovelCreateResponseDto;
import com.webnovel.novel.entity.Novel;
import com.webnovel.novel.service.NovelService;
import com.webnovel.security.jwt.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/novels")
public class NovelController {

    private final NovelService novelService;

    @PostMapping
    public ResponseEntity<ResponseDto<NovelCreateResponseDto>> registNovel(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody NovelCreateRequestDto request
            ) {
        NovelCreateResponseDto novel = novelService.createNovel(authUser, request);
        return ResponseDto.of(HttpStatus.CREATED, novel).toEntity();
    }

    @GetMapping
    public ResponseEntity<ResponseDto<List<Novel>>> getNovelList() {
        return null;
    }

    @DeleteMapping("/{novelId}")
    public ResponseEntity<ResponseDto<?>> deleteNovel(@PathVariable Long novelId) {
        return null;
    }

    @PatchMapping("/{novelId}")
    public ResponseEntity<ResponseDto<?>> updateNovel(@PathVariable Long novelId) {
        return null;
    }

    @PostMapping("/{novelId}/episodes")
    public ResponseEntity<ResponseDto<?>> registEpisode(
            @PathVariable Long novelId) {
        return null;
    }
}
