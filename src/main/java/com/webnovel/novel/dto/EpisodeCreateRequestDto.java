package com.webnovel.novel.dto;

import lombok.Data;

@Data
public class EpisodeCreateRequestDto {
    private String title;
    private String authorReview;
    private String content;
}
