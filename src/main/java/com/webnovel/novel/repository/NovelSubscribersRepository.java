package com.webnovel.novel.repository;

import com.webnovel.novel.entity.NovelSubscribers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NovelSubscribersRepository extends JpaRepository<NovelSubscribers, Long> {
}
