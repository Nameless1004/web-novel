package com.webnovel.novel.entity;

import com.webnovel.novel.dto.NovelUpdateDto;
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

    @ManyToOne(fetch = FetchType.LAZY)
    private User author;

    private String title;
    private String synopsis;

    @Enumerated(EnumType.STRING)
    private NovelStatus status;

    private LocalDateTime publishedAt;
    private LocalDateTime lastUpdatedAt;

    @OneToMany(mappedBy = "novel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NovelTags> tags = new ArrayList<>();


    public Novel(User author, String title, String synopsis, NovelStatus status, LocalDateTime publishedAt) {
        this.author = author;
        this.title = title;
        this.synopsis = synopsis;
        this.status = status;
        this.publishedAt = publishedAt;
    }

    private void updateLastUpdatedAt() {
        lastUpdatedAt = LocalDateTime.now();
    }

    public void update(NovelUpdateDto request, List<NovelTags> newTags) {
        this.title = request.getTitle();
        this.synopsis = request.getSynopsis();
        this.status = request.getStatus();
        this.tags.clear();
        this.tags.addAll(newTags);
        updateLastUpdatedAt();
    }
}
