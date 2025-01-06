package com.webnovel.domain.novel.repository;

import com.webnovel.domain.novel.entity.NovelSubscribers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NovelSubscribersRepository extends JpaRepository<NovelSubscribers, Long> {
}
