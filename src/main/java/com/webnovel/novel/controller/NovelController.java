package com.webnovel.novel.controller;

import com.webnovel.novel.service.NovelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NovelController {

    private final NovelService novelService;

    @GetMapping("/")
    public void test() {

    }
}
