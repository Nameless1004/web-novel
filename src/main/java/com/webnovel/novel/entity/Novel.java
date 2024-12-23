package com.webnovel.novel.entity;

import com.webnovel.novel.enums.NovelStatus;
import com.webnovel.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @OneToMany(mappedBy = "novel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NovelTag> novelTags = new ArrayList<>();

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
        updateLastUpdatedAt();
    }

    public void removeEpisode(Episode episode) {
        episodes.remove(episode);
        episode.setNovel(null);

        // 회차 삭제 시 업데이트
        updateLastUpdatedAt();
    }

    private void updateLastUpdatedAt() {
        lastUpdatedAt = LocalDateTime.now();
    }
}
