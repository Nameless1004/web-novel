package com.webnovel.novel.service;

import com.webnovel.novel.dto.NovelCreateRequestDto;
import com.webnovel.novel.dto.NovelCreateResponseDto;
import com.webnovel.novel.dto.NovelInfoResponseDto;

import java.awt.print.Pageable;

public interface NovelService {

    NovelCreateResponseDto createNovel(NovelCreateRequestDto request);
    void deleteNovel(int novelId);

    NovelInfoResponseDto getNovelDetails(long novelId);
}
