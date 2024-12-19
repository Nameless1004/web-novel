package com.webnovel.novel.entity;

import jakarta.persistence.*;

@Entity
public class NovelTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "novel_id")
    private Novel novel;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;
}
