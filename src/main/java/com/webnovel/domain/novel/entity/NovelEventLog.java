package com.webnovel.domain.novel.entity;

import com.webnovel.domain.novel.enums.NovelStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NovelEventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Novel novel;

    private NovelStatus status;

    private LocalDateTime timestamp;

    public NovelEventLog(Novel novel) {
        this.novel = novel;
        this.status = novel.getStatus();
        this.timestamp = LocalDateTime.now();
    }
}
