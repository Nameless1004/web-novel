package com.webnovel.domain.novel.entity;

import com.webnovel.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EpisodeViewLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Novel novel;

    @ManyToOne(fetch = FetchType.LAZY)
    private Episode episode;

    private int hour;
    private LocalDateTime timestamp;

    public EpisodeViewLog(User user, Episode episode) {
        this.user = user;
        this.novel = episode.getNovel();
        this.episode = episode;
        this.timestamp = LocalDateTime.now();
        this.hour = timestamp.getHour();
    }
}
