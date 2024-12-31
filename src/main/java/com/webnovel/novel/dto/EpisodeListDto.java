package com.webnovel.novel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EpisodeListDto {

    private long id;
    private int episodeNumber;
    private long viewCount;
    private long recommendationCount;
    private long commentCount;
    private String title;
    private String content;
    private LocalDateTime createdAt;
}
