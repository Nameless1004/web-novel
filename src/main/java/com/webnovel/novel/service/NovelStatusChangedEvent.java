package com.webnovel.novel.service;

import com.webnovel.novel.entity.Novel;
import org.springframework.context.ApplicationEvent;

public class NovelStatusChangedEvent extends ApplicationEvent {
    private final Novel novel;

    public NovelStatusChangedEvent(Novel novel) {
        super(novel);
        this.novel = novel;
    }

    public Novel getNovel() {
        return novel;
    }
}
