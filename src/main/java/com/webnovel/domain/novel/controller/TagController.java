package com.webnovel.domain.novel.controller;

import com.webnovel.common.dto.ResponseDto;
import com.webnovel.domain.novel.dto.TagDetailsDto;
import com.webnovel.domain.novel.service.TagServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tags")
public class TagController {

    private final TagServiceImpl tagService;

    @GetMapping
    public ResponseEntity<ResponseDto<List<TagDetailsDto>>> getAllTags() {
        return tagService.getAllTags().toEntity();
    }
}
