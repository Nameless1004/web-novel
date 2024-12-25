package com.webnovel.novel.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NovelTags {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Novel novel;

    @ManyToOne(fetch = FetchType.LAZY)
    private Tag tag;

    public NovelTags(Novel novel, Tag tag) {
        this.novel = novel;
        this.tag = tag;
    }
}
