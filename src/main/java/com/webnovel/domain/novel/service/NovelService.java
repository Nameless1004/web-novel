package com.webnovel.domain.novel.service;

import com.webnovel.common.dto.CustomPage;
import com.webnovel.common.dto.ResponseDto;
import com.webnovel.domain.novel.dto.*;
import com.webnovel.domain.security.jwt.AuthUser;

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
    ResponseDto<NovelDetailsDto> getNovelDetails(long novelId);

    /**
     * 소설 목록조회
     * @param page
     * @param size
     * @return
     */
    ResponseDto<CustomPage<NovelListDto>> getNovelList(int page, int size);

    ResponseDto<CustomPage<HotNovelResponseDto>> getRealtimeHotNovels(String option, int hour, int page, int size);

    /**
     * 내가 작성한 소설 목록 조회
     * @param authUser
     * @param page
     * @param size
     * @return
     */
    ResponseDto<CustomPage<NovelListDto>> getMyNovelList(AuthUser authUser, int page, int size);
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
}
