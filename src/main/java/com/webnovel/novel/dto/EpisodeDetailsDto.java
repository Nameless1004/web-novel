package com.webnovel.novel.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class EpisodeDetailsDto {

    private long id;
    private int episodeNumber;
    private long viewCount;
    private long recommendationCount;
    private long commentCount;
    private String title;
    private String content;
}
