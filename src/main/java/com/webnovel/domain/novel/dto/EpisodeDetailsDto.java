package com.webnovel.domain.novel.dto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeDetailsDto {

    private long id;
    private long novelId;
    private int episodeNumber;
    private long viewCount;
    private long recommendationCount;
    private long commentCount;
    private String title;
    private String content;
    private String authorReview;
    private String coverImageUrl;

    @Setter
    private Long prevEpisodeId;
    @Setter
    private Long nextEpisodeId;

    public EpisodeDetailsDto(long id, long novelId, int episodeNumber, long viewCount, long recommendationCount, long commentCount, String title, String content, String authorReview, String coverImageUrl) {
        this.id = id;
        this.novelId = novelId;
        this.episodeNumber = episodeNumber;
        this.viewCount = viewCount;
        this.recommendationCount = recommendationCount;
        this.commentCount = commentCount;
        this.title = title;
        this.content = content;
        this.authorReview = authorReview;
        this.coverImageUrl = coverImageUrl;
    }
}
