package com.webnovel.novel.service;

import com.webnovel.common.dto.CustomPage;
import com.webnovel.common.dto.ResponseDto;
import com.webnovel.novel.dto.EpisodeCreateRequestDto;
import com.webnovel.novel.dto.EpisodeDetailsDto;
import com.webnovel.novel.dto.EpisodeListDto;
import com.webnovel.novel.dto.EpisodeUpdateDto;
import com.webnovel.novel.entity.Episode;
import com.webnovel.security.jwt.AuthUser;
import com.webnovel.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;

public interface EpisodeService {

    /**
     * 에피소드 등록
     * @param authUser
     * @param novelId
     * @param requestDto
     * @return
     */
    ResponseDto<Void> addEpisode(AuthUser authUser, long novelId, EpisodeCreateRequestDto requestDto);

    /**
     * 에피소드 수정
     * @param authUser
     * @param novelId
     * @param episodeId
     * @param updateDto
     * @return
     */
    ResponseDto<Void> updateEpisode(AuthUser authUser, long novelId, long episodeId, EpisodeUpdateDto updateDto);

    /**
     * 에피소드 삭제
     * @param novelId
     * @param episodeId
     * @return
     */
    ResponseDto<Void> deleteEpisode(AuthUser authUser, long novelId, long episodeId);

    ResponseDto<CustomPage<EpisodeListDto>> getEpisodeList(int page, int size, long novelId);
    ResponseDto<EpisodeDetailsDto> getEpisodeDetails(AuthUser authUser, long episodeId);

    void increaseViewCount(AuthUser authUser, long episodeId, HttpServletRequest request);
    long getViewCount(long episodeId);

    // 추천수
    long increaseRecommendationCount(AuthUser user, long episodeId);
    long getRecommendationCount(long episodeId);

    void stampViewLog(User user, Episode episodeId);
}
