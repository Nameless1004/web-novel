package com.webnovel.novel.service;

import com.webnovel.novel.dto.NovelCreateRequestDto;
import com.webnovel.novel.dto.NovelInfoResponseDto;

import java.awt.print.Pageable;

public interface NovelService {

    void createNovel(NovelCreateRequestDto request);
    void deleteNovel(int novelId);

    NovelInfoResponseDto getNovelDetails(long novelId);
}
