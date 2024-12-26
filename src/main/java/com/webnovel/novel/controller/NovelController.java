package com.webnovel.novel.controller;

import com.webnovel.common.dto.CustomPage;
import com.webnovel.common.dto.ResponseDto;
import com.webnovel.novel.dto.*;
import com.webnovel.novel.entity.Novel;
import com.webnovel.novel.service.NovelService;
import com.webnovel.security.jwt.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/novels")
public class NovelController {

    private final NovelService novelService;

    // ---------------------
    //      소설 CRUD
    // ---------------------
    @PostMapping
    public ResponseEntity<ResponseDto<NovelCreateResponseDto>> registNovel(
            @AuthenticationPrincipal AuthUser authUser,
            @Validated @RequestBody NovelCreateRequestDto request ) {
        return novelService.createNovel(authUser, request)
                .toEntity();
    }

    @PostMapping("/{novelId}/preferences")
    public ResponseEntity<ResponseDto<Void>> registPreferenceNovel(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable long novelId ) {
        return novelService.registPreferenceNovel(authUser, novelId)
                .toEntity();
    }

    @GetMapping
    public ResponseEntity<ResponseDto<CustomPage<NovelListDto>>> getNovelList(
            @RequestParam int page,
            @RequestParam int size) {
        return novelService.getNovelList(page, size)
                .toEntity();
    }

    @DeleteMapping("/{novelId}")
    public ResponseEntity<ResponseDto<Void>> deleteNovel(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long novelId ) {
        return novelService.deleteNovel(authUser, novelId)
                .toEntity();
    }

    @PatchMapping("/{novelId}")
    public ResponseEntity<ResponseDto<Void>> updateNovel(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long novelId,
            @RequestBody NovelUpdateDto updateDto) {
        return novelService.updateNovel(authUser, novelId, updateDto)
                .toEntity();
    }


    //-----------------------
    //     에피 소드 CRUD
    //-----------------------
    @PostMapping("/{novelId}/episodes")
    public ResponseEntity<ResponseDto<?>> registEpisode(
            @PathVariable Long novelId,
            @Validated @RequestBody EpisodeCreateRequestDto request) {
        return null;
    }
}
