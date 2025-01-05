package com.webnovel.novel.repository;

import com.webnovel.novel.entity.NovelEventLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NovelEventLogRepository extends JpaRepository<NovelEventLog, Long> {
}
