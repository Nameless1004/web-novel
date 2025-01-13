package com.webnovel.domain.novel.dto;

import com.webnovel.domain.novel.entity.Novel;
import com.webnovel.domain.novel.entity.NovelTags;
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
    private String coverImageUrl;
    private List<String> tags;

    public NovelListDto(Novel novel) {
        novelId = novel.getId();
        title = novel.getTitle();
        authorNickname = novel.getAuthor().getNickname();
        publishDate = novel.getPublishedAt().toLocalDate();
        lastUpdateDate = novel.getLastUpdatedAt().toLocalDate();
        coverImageUrl = novel.getCoverImageUrl();
        tags = novel.getTags().stream().map(x->x.getTag().getName()).toList();
    }

    public NovelListDto(long novelId, String title, String authorNickname, LocalDateTime publishedDate, LocalDateTime lastUpdatedDate, String coverImageUrl) {
        this.novelId = novelId;
        this.title = title;
        this.authorNickname = authorNickname;
        this.publishDate = publishedDate.toLocalDate();
        this.lastUpdateDate = lastUpdatedDate.toLocalDate();
        this.coverImageUrl = coverImageUrl;
        tags = new ArrayList<>();
    }

    public void addTags(List<String> tags) {
        this.tags.addAll(tags);
    }
}
