package com.webnovel.novel.repository;

import com.webnovel.novel.entity.NovelTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NovelTagRepository extends JpaRepository<NovelTag, Long> {
}
