package com.webnovel.domain.novel.controller;

import com.webnovel.common.dto.CustomPage;
import com.webnovel.common.dto.ResponseDto;
import com.webnovel.domain.novel.dto.*;
import com.webnovel.domain.novel.service.EpisodeService;
import com.webnovel.domain.security.jwt.AuthUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class EpisodeController {

    private final EpisodeService episodeService;

    //-----------------------
    //     에피 소드 CRUD
    //-----------------------
    @PostMapping("/novels/{novelId}/episodes")
    public ResponseEntity<ResponseDto<Void>> registEpisode(
            @PathVariable Long novelId,
            @AuthenticationPrincipal AuthUser authUser,
            @Validated @RequestBody EpisodeCreateRequestDto request) {

        return episodeService.addEpisode(authUser, novelId, request)
                .toEntity();
    }

    @PatchMapping("/novels/{novelId}/episodes/{episodeId}")
    public ResponseEntity<ResponseDto<Void>> updateEpisode(
            @PathVariable Long novelId,
            @PathVariable Long episodeId,
            @AuthenticationPrincipal AuthUser authUser,
            @Validated @RequestBody EpisodeUpdateDto request) {

        return episodeService.updateEpisode(authUser, novelId,episodeId, request)
                .toEntity();
    }

    @DeleteMapping("/novels/{novelId}/episodes/{episodeId}")
    public ResponseEntity<ResponseDto<Void>> deleteEpisode(
            @PathVariable Long novelId,
            @PathVariable Long episodeId,
            @AuthenticationPrincipal AuthUser authUser) {

        return episodeService.deleteEpisode(authUser, novelId, episodeId)
                .toEntity();
    }

    @PatchMapping("/episodes/{episodeId}/views")
    public ResponseEntity<ResponseDto<Void>> increaseViewCount(
            @PathVariable long episodeId,
            @AuthenticationPrincipal AuthUser authUser,
            HttpServletRequest request) {
        episodeService.increaseViewCount(authUser, episodeId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/novels/{novelId}/episodes")
    public ResponseEntity<ResponseDto<CustomPage<EpisodeListDto>>> getEpisodes(
            @Min(value = 1, message = "페이지는 1보다 커야합니다.")
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable long novelId) {
        return episodeService.getEpisodeList(page, size, novelId)
                .toEntity();
    }

    @GetMapping("/episodes/{episodeId}")
    public ResponseEntity<ResponseDto<EpisodeDetailsDto>> getEpisode(
            @PathVariable long episodeId,
            @AuthenticationPrincipal AuthUser authUser) {
        return episodeService.getEpisodeDetails(authUser, episodeId)
                .toEntity();
    }


    @GetMapping("/episodes/{episodeId}/views")
    public ResponseEntity<ResponseDto<ViewCountDto>> getViewCount(
            @PathVariable long episodeId) {

        return ResponseDto.of(HttpStatus.OK, new ViewCountDto(episodeService.getViewCount(episodeId)))
                .toEntity();
    }

    @PatchMapping("/episodes/{episodeId}/recommendations")
    public ResponseEntity<ResponseDto<RecommendationCount>> increaseRecommendationCount(
            @PathVariable long episodeId,
            @AuthenticationPrincipal AuthUser authUser ) {

        return ResponseDto.of(HttpStatus.OK, new RecommendationCount(episodeService.increaseRecommendationCount(authUser, episodeId)))
                .toEntity();
    }

    @GetMapping("/episodes/{episodeId}/recommendations")
    public ResponseEntity<ResponseDto<RecommendationCount>> getRecommendationCount(
            @PathVariable long episodeId ) {
        return ResponseDto.of(HttpStatus.OK, new RecommendationCount(episodeService.getRecommendationCount(episodeId)))
                .toEntity();
    }
}
