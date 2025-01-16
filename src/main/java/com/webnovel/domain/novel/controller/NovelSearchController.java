package com.webnovel.domain.novel.controller;

import com.webnovel.common.dto.CustomPage;
import com.webnovel.common.dto.ResponseDto;
import com.webnovel.domain.novel.dto.NovelListDto;
import com.webnovel.domain.novel.service.NovelSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NovelSearchController {

    private final NovelSearchService novelSearchService;

    @GetMapping("/novels/search")
    public ResponseEntity<ResponseDto<CustomPage<NovelListDto>>> findNovelListByKeyword (
            @RequestParam(name="oc", required = false, defaultValue = "new") String order,
            @RequestParam(name="sort_dir", required = false, defaultValue = "desc") String direction,
            @RequestParam(name="keyword") String keyword,
            @RequestParam(name = "sc") String searchCondition,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size){

        return novelSearchService.searchNovelByKeyword(searchCondition, order, direction, keyword, page, size)
                .toEntity();
    }
}
