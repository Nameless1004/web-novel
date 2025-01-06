package com.webnovel.domain.novel.repository;

import com.webnovel.common.dto.CustomPage;
import com.webnovel.domain.novel.dto.HotNovelResponseDto;
import com.webnovel.domain.novel.dto.NovelDetailsDto;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface NovelCustomRepository {

    Optional<NovelDetailsDto> getNovelDetails(long novelId);
    CustomPage<HotNovelResponseDto> getRealtimeHotNovelList(String option, int hour, Pageable pageable);
}
