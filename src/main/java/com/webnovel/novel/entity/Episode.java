package com.webnovel.novel.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Episode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    private String content;

    private int episodeNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "novel_id")
    private Novel novel;

    public void setNovel(Novel novel) {
        this.novel = novel;
    }
}
