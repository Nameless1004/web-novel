package com.webnovel.novel.entity;

import com.webnovel.common.entity.Timestamped;
import com.webnovel.novel.dto.EpisodeUpdateDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Episode extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title; // 에피소드 제목

    private String authorReview; // 작가 후기

    @Lob
    private String content; // 글

    private int episodeNumber; // 회차

    private long viewCount; // 조회수
    private long recommendationCount; // 추천수

    // 커버 이미지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "novel_id")
    private Novel novel;

    public Episode(Novel novel, String title, String authorReview, String content, int episodeNumber) {
        this.novel = novel;
        this.title = title;
        this.authorReview = authorReview;
        this.content = content;
        this.episodeNumber = episodeNumber;
        this.viewCount = 0;
        this.recommendationCount = 0;
    }

    public void update(EpisodeUpdateDto updateDto) {
        this.title = updateDto.getTitle();
        this.authorReview = updateDto.getAuthorReview();
        this.content = updateDto.getContent();
    }
}
