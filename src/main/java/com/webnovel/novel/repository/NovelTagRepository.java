package com.webnovel.novel.repository;

import com.webnovel.novel.entity.Novel;
import com.webnovel.novel.entity.NovelTags;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NovelTagRepository extends JpaRepository<NovelTags, Long> {
    void deleteAllByNovel(Novel novel);
}
