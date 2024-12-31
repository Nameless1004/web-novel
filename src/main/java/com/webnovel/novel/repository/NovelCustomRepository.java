package com.webnovel.novel.repository;

import com.webnovel.novel.dto.NovelDetailsDto;

import java.util.Optional;

public interface NovelCustomRepository {

    Optional<NovelDetailsDto> getNovelDetails(long novelId);
}
