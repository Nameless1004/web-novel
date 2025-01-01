package com.webnovel.novel.controller;

import com.webnovel.common.dto.CustomPage;
import com.webnovel.common.dto.ResponseDto;
import com.webnovel.novel.dto.*;
import com.webnovel.novel.service.EpisodeService;
import com.webnovel.security.jwt.AuthUser;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/novels")
@RequiredArgsConstructor
@Validated
public class EpisodeController {

    private final EpisodeService episodeService;

    //-----------------------
    //     에피 소드 CRUD
    //-----------------------
    @PostMapping("/{novelId}/episodes")
    public ResponseEntity<ResponseDto<Void>> registEpisode(
            @PathVariable Long novelId,
            @AuthenticationPrincipal AuthUser authUser,
            @Validated @RequestBody EpisodeCreateRequestDto request) {

        return episodeService.addEpisode(authUser, novelId, request)
                .toEntity();
    }

    @PatchMapping("/{novelId}/episodes/{episodeId}")
    public ResponseEntity<ResponseDto<Void>> updateEpisode(
            @PathVariable Long novelId,
            @PathVariable Long episodeId,
            @AuthenticationPrincipal AuthUser authUser,
            @Validated @RequestBody EpisodeUpdateDto request) {

        return episodeService.updateEpisode(authUser, novelId,episodeId, request)
                .toEntity();
    }

    @DeleteMapping("/{novelId}/episodes/{episodeId}")
    public ResponseEntity<ResponseDto<Void>> deleteEpisode(
            @PathVariable Long novelId,
            @PathVariable Long episodeId,
            @AuthenticationPrincipal AuthUser authUser) {

        return episodeService.deleteEpisode(authUser, novelId, episodeId)
                .toEntity();
    }

    @PatchMapping("/{novelId}/episodes/{episodeId}/views")
    public ResponseEntity<ResponseDto<ViewCountDto>> increaseViewCount(
            @PathVariable long novelId,
            @PathVariable long episodeId) throws InterruptedException {

        return ResponseDto.of(HttpStatus.OK, new ViewCountDto(episodeService.increaseViewCount(episodeId)))
                .toEntity();
    }

    @GetMapping("/{novelId}/episodes")
    public ResponseEntity<ResponseDto<CustomPage<EpisodeListDto>>> getEpisodes(
            @Min(value = 1, message = "페이지는 1보다 커야합니다.")
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable long novelId) {
        return episodeService.getEpisodeList(page, size, novelId)
                .toEntity();
    }

    @GetMapping("/{novelId}/episodes/{episodeId}")
    public ResponseEntity<ResponseDto<EpisodeDetailsDto>> getEpisode(
            @PathVariable long novelId,
            @PathVariable long episodeId,
            @AuthenticationPrincipal AuthUser authUser) {
        return episodeService.getEpisodeDetails(authUser, episodeId)
                .toEntity();
    }

    @PatchMapping("/{novelId}/episodes/{episodeId}/views/v2")
    public ResponseEntity<ResponseDto<ViewCountDto>> increaseViewCountNoncache(
            @PathVariable long novelId,
            @PathVariable long episodeId) {

        return ResponseDto.of(HttpStatus.OK, new ViewCountDto(episodeService.increaseViewCountNoncache(episodeId)))
                .toEntity();
    }

    /**
     * 비관적 락 증가
     * @param novelId
     * @param episodeId
     * @return
     */
    @PatchMapping("/{novelId}/episodes/{episodeId}/views/v3")
    public ResponseEntity<ResponseDto<ViewCountDto>> increaseViewCountPessimistic(
            @PathVariable long novelId,
            @PathVariable long episodeId) {

        return ResponseDto.of(HttpStatus.OK, new ViewCountDto(episodeService.increaseViewCountPessimisticLock(episodeId)))
                .toEntity();
    }

    /**
     * 낙관적 락 증가
     * @param novelId
     * @param episodeId
     * @return
     */
    @PatchMapping("/{novelId}/episodes/{episodeId}/views/v4")
    public ResponseEntity<ResponseDto<ViewCountDto>> increaseViewCountOptimistic(
            @PathVariable long novelId,
            @PathVariable long episodeId) throws InterruptedException {

        while(true) {
            try {
                long l = episodeService.increaseViewCountOptimisticLock(episodeId);
                return ResponseDto.of(HttpStatus.OK, new ViewCountDto(l))
                        .toEntity();
            } catch (Exception e) {
                Thread.sleep(50);
            }
        }
    }

    @GetMapping("/{novelId}/episodes/{episodeId}/views")
    public ResponseEntity<ResponseDto<ViewCountDto>> getViewCount(
            @PathVariable long novelId,
            @PathVariable long episodeId) {

        return ResponseDto.of(HttpStatus.OK, new ViewCountDto(episodeService.getViewCount(episodeId)))
                .toEntity();
    }

    @GetMapping("/{novelId}/episodes/{episodeId}/views/v2")
    public ResponseEntity<ResponseDto<ViewCountDto>> getViewCountNoncache(
            @PathVariable long novelId,
            @PathVariable long episodeId) {

        return ResponseDto.of(HttpStatus.OK, new ViewCountDto(episodeService.getViewCountNoncache(episodeId)))
                .toEntity();
    }

    @PatchMapping("/{novelId}/episodes/{episodeId}/recommendations")
    public ResponseEntity<ResponseDto<ViewCountDto>> increaseRecommendationCount(
            @PathVariable long novelId,
            @PathVariable long episodeId,
            @AuthenticationPrincipal AuthUser authUser ) {

        return ResponseDto.of(HttpStatus.OK, new ViewCountDto(episodeService.increaseRecommendationCount(authUser, episodeId)))
                .toEntity();
    }

    @GetMapping("/{novelId}/episodes/{episodeId}/recommendations")
    public ResponseEntity<ResponseDto<ViewCountDto>> getRecommendationCount(
            @PathVariable long novelId,
            @PathVariable long episodeId ) {
        return ResponseDto.of(HttpStatus.OK, new ViewCountDto(episodeService.getRecommendationCount(episodeId)))
                .toEntity();
    }
}
