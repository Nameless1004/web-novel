package com.webnovel.novel.controller;

import com.webnovel.common.dto.CustomPage;
import com.webnovel.common.dto.ResponseDto;
import com.webnovel.novel.dto.*;
import com.webnovel.novel.service.EpisodeService;
import com.webnovel.novel.service.NovelService;
import com.webnovel.security.jwt.AuthUser;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class NovelController {

    private final NovelService novelService;

    // ---------------------
    //      소설 CRUD
    // ---------------------
    @PostMapping("/novels")
    public ResponseEntity<ResponseDto<NovelCreateResponseDto>> registNovel(
            @AuthenticationPrincipal AuthUser authUser,
            @Validated @RequestBody NovelCreateRequestDto request ) {
        return novelService.createNovel(authUser, request)
                .toEntity();
    }

    @PostMapping("/novels/{novelId}/preferences")
    public ResponseEntity<ResponseDto<Void>> registPreferenceNovel(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable long novelId ) {
        return novelService.registPreferenceNovel(authUser, novelId)
                .toEntity();
    }

    @GetMapping("/novels")
    public ResponseEntity<ResponseDto<CustomPage<NovelListDto>>> getNovelList(
            @RequestParam int page,
            @RequestParam int size) {
        return novelService.getNovelList(page, size)
                .toEntity();
    }

    /**
     * 내가 작성한 소설 목록 가져오기
     * @param authUser
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/user/novels")
    public ResponseEntity<ResponseDto<CustomPage<NovelListDto>>> getNovelList(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestParam int page,
            @RequestParam int size) {
        return novelService.getMyNovelList(authUser, page, size)
                .toEntity();
    }

    /**
     * 인기 작품
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/novels/hot")
    public ResponseEntity<ResponseDto<CustomPage<HotNovelResponseDto>>> getHotNovelList(
            @RequestParam(required = true, defaultValue = "view") String option,
            @Range(min = 0, max= 23, message = "시간은 0시보다 작거나 23시보다 클 수 없습니다.")
            @RequestParam(required = true) int hour,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return novelService.getRealtimeHotNovels(option,hour, page, size).toEntity();
    }

    @GetMapping("/novels/{novelId}")
    public ResponseEntity<ResponseDto<NovelDetailsDto>> getNovelList(
            @PathVariable long novelId) {
        return novelService.getNovelDetails(novelId)
                .toEntity();
    }

    @DeleteMapping("/novels/{novelId}")
    public ResponseEntity<ResponseDto<Void>> deleteNovel(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long novelId ) {
        return novelService.deleteNovel(authUser, novelId)
                .toEntity();
    }

    @PatchMapping("/novels/{novelId}")
    public ResponseEntity<ResponseDto<Void>> updateNovel(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long novelId,
            @RequestBody NovelUpdateDto updateDto) {
        return novelService.updateNovel(authUser, novelId, updateDto)
                .toEntity();
    }
}
