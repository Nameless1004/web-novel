package com.webnovel.domain.novel.service;

import com.webnovel.common.dto.CustomPage;
import com.webnovel.common.dto.ResponseDto;
import com.webnovel.domain.novel.dto.NovelListDto;

public interface NovelSearchService {

    ResponseDto<CustomPage<NovelListDto>> searchNovelByKeyword(String searchCondition, String orderCondition, String direction, String keyword, int page, int size);
}
