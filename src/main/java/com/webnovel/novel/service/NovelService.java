package com.webnovel.novel.service;

import com.webnovel.common.dto.CustomPage;
import com.webnovel.common.dto.ResponseDto;
import com.webnovel.novel.dto.*;
import com.webnovel.novel.entity.Novel;
import com.webnovel.security.jwt.AuthUser;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface NovelService {

    /**
     * 소설 생성
     * @param authUser
     * @param request
     * @return
     */
    ResponseDto<NovelCreateResponseDto> createNovel(AuthUser authUser, NovelCreateRequestDto request);

    /**
     * 소설 정보 업데이트
     * @param authUser
     * @param novelId
     * @param request
     * @return
     */
    ResponseDto<Void> updateNovel(AuthUser authUser, long novelId, NovelUpdateDto request);

    /**
     * 소설 삭제
     * @param novelId
     * @return
     */
    ResponseDto<Void> deleteNovel(AuthUser authUser, long novelId);

    /**
     * 소설 상세조회
     * @param novelId
     * @return
     */
    ResponseDto<NovelInfoResponseDto> getNovelDetails(long novelId);

    /**
     * 알림 등록
     * @param authUser
     * @param novelId
     * @return
     */
    ResponseDto<Void> subscribeNovel(AuthUser authUser, long novelId);

    /**
     * 선호 작품 등록
     * @param authUser
     * @param novelId
     * @return
     */
    ResponseDto<Void> registPreferenceNovel(AuthUser authUser, long novelId);
  
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
    ResponseDto<Void> deleteEpisode(long novelId, long episodeId);

    ResponseDto<CustomPage<NovelListDto>> getNovelList(int page, int size);
}
