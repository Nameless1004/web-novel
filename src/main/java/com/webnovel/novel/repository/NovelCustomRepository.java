package com.webnovel.novel.repository;

import com.webnovel.common.dto.CustomPage;
import com.webnovel.novel.dto.HotNovelResponseDto;
import com.webnovel.novel.dto.NovelDetailsDto;
import com.webnovel.novel.dto.NovelListDto;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface NovelCustomRepository {

    Optional<NovelDetailsDto> getNovelDetails(long novelId);
    CustomPage<HotNovelResponseDto> getRealtimeHotNovelList(String option, int hour, Pageable pageable);
}
