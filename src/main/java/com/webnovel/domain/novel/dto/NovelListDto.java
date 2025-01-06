package com.webnovel.domain.novel.dto;

import com.webnovel.domain.novel.entity.Novel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NovelListDto {

    private long novelId;
    private String title;
    private String authorNickname;
    private List<String> tags;

    public NovelListDto(Novel novel) {
        novelId = novel.getId();
        title = novel.getTitle();
        authorNickname = novel.getAuthor().getNickname();
        tags = new ArrayList<>();
    }
}
