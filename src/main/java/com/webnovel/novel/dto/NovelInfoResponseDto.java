package com.webnovel.novel.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class NovelInfoResponseDto {
    private long novelId;
    private String title;
    private String author;
    private String summary;
    private String novelStatus;
    private long viewCount;
    private long episodeCount;
    private long recommendCount;
    private long preferenceCount;
    private long notificationCount;
    private List<String> tags;
}
