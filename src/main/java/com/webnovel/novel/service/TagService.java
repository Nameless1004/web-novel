package com.webnovel.novel.service;

import com.webnovel.common.dto.ResponseDto;
import com.webnovel.novel.dto.TagDetailsDto;
import org.springframework.http.HttpStatus;

import java.util.List;

public interface TagService {
    ResponseDto<List<TagDetailsDto>> getAllTags();
}
