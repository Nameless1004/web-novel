package com.webnovel.domain.novel.repository;

import com.webnovel.domain.novel.entity.Novel;
import com.webnovel.domain.novel.entity.NovelTags;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NovelTagRepository extends JpaRepository<NovelTags, Long> {
    void deleteAllByNovel(Novel novel);
}
