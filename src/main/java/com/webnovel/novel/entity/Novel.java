package com.webnovel.novel.entity;

import com.webnovel.common.entity.Timestamped;
import com.webnovel.novel.dto.NovelCreateRequestDto;
import com.webnovel.novel.enums.NovelStatus;
import com.webnovel.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Novel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User author;

    private String title;
    private String summary;

    @Enumerated(EnumType.STRING)
    private NovelStatus status;

    private LocalDateTime publishedAt;
    private LocalDateTime lastUpdatedAt;

    // novel 삭제되면 episode도 같이 삭제되게 영속성 전이
    @OneToMany(mappedBy = "novel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Episode> episodes = new ArrayList<>();

    public Novel(User author, String title, String summary, NovelStatus status, LocalDateTime publishedAt) {
        this.author = author;
        this.title = title;
        this.summary = summary;
        this.status = status;
        this.publishedAt = publishedAt;
    }

    public void addEpisode(Episode episode) {
        episodes.add(episode);
        episode.setNovel(this);
        // 회차 추가 시 업데이트
        lastUpdatedAt = LocalDateTime.now();
    }

    public void removeEpisode(Episode episode) {
        episodes.remove(episode);
        episode.setNovel(null);

        // 회차 삭제 시 업데이트
        lastUpdatedAt = LocalDateTime.now();
    }
}
