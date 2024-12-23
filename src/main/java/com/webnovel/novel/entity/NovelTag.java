package com.webnovel.novel.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
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
