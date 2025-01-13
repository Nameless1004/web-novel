package com.webnovel.domain.novel.repository;

import com.querydsl.core.types.Order;
import com.webnovel.common.dto.CustomPage;
import com.webnovel.domain.novel.dto.HotNovelResponseDto;
import com.webnovel.domain.novel.dto.NovelDetailsDto;
import com.webnovel.domain.novel.dto.NovelListDto;
import com.webnovel.domain.novel.dto.NovelOrderCondition;
import com.webnovel.domain.security.jwt.AuthUser;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface NovelCustomRepository {

    CustomPage<NovelListDto> getNovelList(NovelOrderCondition orderCondition, Order order, Pageable pageable);
    Optional<NovelDetailsDto> getNovelDetails(long novelId);
    CustomPage<HotNovelResponseDto> getRealtimeHotNovelList(String option, int hour, Pageable pageable);

    CustomPage<NovelListDto> getMyNovels(AuthUser authUser, Pageable pageable);
}
