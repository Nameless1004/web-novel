package com.webnovel.domain.novel.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class NovelDetailsDto {

    private Long id;
    private String title;
    private String synopsis;
    private String authorNickname;
    private List<String> tags;
    private long totalSubscriberCount;
    private long totalPreferenceCount;
    private long totalViewCount;
    private long totalRecommendationCount;
    private long totalEpisodeCount;
    private long totalCommentCount;

    public NovelDetailsDto(Long id, String title, String synopsis, String authorNickname,
                           long totalSubscriberCount, long totalPreferenceCount, long totalViewCount,
                           long totalRecommendationCount, long totalEpisodeCount, long totalCommentCount) {
        this.id = id;
        this.title = title;
        this.synopsis = synopsis;
        this.tags = new ArrayList<>();
        this.authorNickname = authorNickname;
        this.totalSubscriberCount = totalSubscriberCount;
        this.totalPreferenceCount = totalPreferenceCount;
        this.totalViewCount = totalViewCount;
        this.totalRecommendationCount = totalRecommendationCount;
        this.totalEpisodeCount = totalEpisodeCount;
        this.totalCommentCount = totalCommentCount;
    }

    public void addTag(List<String> tag) {
        tags.addAll(tag);
    }
}
