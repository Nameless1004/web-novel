package com.webnovel.domain.novel.dto;

import com.webnovel.domain.novel.entity.Novel;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class NovelListDto {

    private long novelId;
    private String title;
    private String authorNickname;
    private LocalDate publishDate;
    private LocalDate lastUpdateDate;
    private List<String> tags;

    public NovelListDto(Novel novel) {
        novelId = novel.getId();
        title = novel.getTitle();
        authorNickname = novel.getAuthor().getNickname();
        tags = new ArrayList<>();
    }

    public NovelListDto(long novelId, String title, String authorNickname, LocalDateTime publishedDate, LocalDateTime lastUpdatedDate) {
        this.novelId = novelId;
        this.title = title;
        this.authorNickname = authorNickname;
        this.publishDate = publishedDate.toLocalDate();
        this.lastUpdateDate = lastUpdatedDate.toLocalDate();
        tags = new ArrayList<>();
    }

    public void addTags(List<String> tags) {
        this.tags.addAll(tags);
    }
}
