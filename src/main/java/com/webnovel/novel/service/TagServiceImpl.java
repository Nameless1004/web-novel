package com.webnovel.novel.service;

import com.webnovel.common.dto.ResponseDto;
import com.webnovel.novel.dto.TagDetailsDto;
import com.webnovel.novel.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public ResponseDto<List<TagDetailsDto>> getAllTags() {
        return ResponseDto.of(HttpStatus.OK, tagRepository.findAll()
                .stream()
                .map(TagDetailsDto::new)
                .toList());
    }
}
