package com.webnovel.domain.novel.repository;

import com.webnovel.domain.novel.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
