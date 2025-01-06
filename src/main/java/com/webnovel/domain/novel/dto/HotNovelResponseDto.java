package com.webnovel.domain.novel.dto;

import com.webnovel.domain.novel.entity.Novel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HotNovelResponseDto {
    private long count;
    private long novelId;
    private String title;
    private String authorNickname;
    private List<String> tags;

    public HotNovelResponseDto(long count, Novel novel, List<String> tags) {
        this.count = count;
        this.novelId = novel.getId();
        this.title = novel.getTitle();
        this.authorNickname = novel.getAuthor().getNickname();
        this.tags = tags == null ? new ArrayList<>() : tags;

    }
}
