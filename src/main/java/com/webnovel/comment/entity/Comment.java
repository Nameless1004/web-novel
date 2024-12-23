package com.webnovel.comment.entity;

import com.webnovel.novel.entity.Episode;
import com.webnovel.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Episode episode;

    @ManyToOne
    private User user;

    private String content;

    private LocalDateTime commentedAt;

    public Comment(Episode episode, User user, String content) {
        this.episode = episode;
        this.user = user;
        this.content = content;
        this.commentedAt = LocalDateTime.now();
    }
}
