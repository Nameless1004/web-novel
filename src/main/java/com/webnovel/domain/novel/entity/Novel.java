package com.webnovel.domain.novel.entity;

import com.webnovel.domain.image.dto.UploadImageInfo;
import com.webnovel.domain.novel.dto.NovelUpdateDto;
import com.webnovel.domain.novel.enums.NovelStatus;
import com.webnovel.domain.user.entity.User;
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
    @JoinColumn(name = "author_id")
    private User author;

    private String title;
    private String synopsis;

    @Enumerated(EnumType.STRING)
    private NovelStatus status;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;

    @OneToMany(mappedBy = "novel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Episode> episodes = new ArrayList<>();

    @OneToMany(mappedBy = "novel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NovelEventLog> eventLogs = new ArrayList<>();

    @OneToMany(mappedBy = "novel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NovelTags> tags = new ArrayList<>();

    @OneToMany(mappedBy = "novel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NovelPreferenceUser> novelPreferenceUsers = new ArrayList<>();

    private String coverImageUrl;
    private String coverImageKey;

    public Novel(User author, String title, String synopsis, NovelStatus status, LocalDateTime publishedAt, UploadImageInfo uploadImageInfo) {
        this.author = author;

        this.title = title;
        this.synopsis = synopsis;
        this.status = status;
        this.publishedAt = publishedAt;
        this.lastUpdatedAt = publishedAt;
        this.coverImageUrl = uploadImageInfo.imageUrl();
        this.coverImageKey = uploadImageInfo.imageKey();
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

    public void updateImage(UploadImageInfo uploadImageInfo) {
        this.coverImageUrl = uploadImageInfo.imageUrl();
        this.coverImageKey = uploadImageInfo.imageKey();
    }
}
