package com.webnovel.novel.service;

import com.webnovel.novel.dto.NovelCreateRequestDto;
import com.webnovel.novel.dto.NovelCreateResponseDto;
import com.webnovel.novel.dto.NovelInfoResponseDto;
import com.webnovel.security.jwt.AuthUser;

import java.awt.print.Pageable;

public interface NovelService {

    NovelCreateResponseDto createNovel(AuthUser authUser, NovelCreateRequestDto request);
    void deleteNovel(int novelId);

    NovelInfoResponseDto getNovelDetails(long novelId);
}
