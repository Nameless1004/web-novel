package com.webnovel.domain.novel.service;

import com.webnovel.common.dto.ResponseDto;
import com.webnovel.domain.novel.dto.TagDetailsDto;

import java.util.List;

public interface TagService {
    ResponseDto<List<TagDetailsDto>> getAllTags();
}
