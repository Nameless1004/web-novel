package com.webnovel.domain.novel.repository;

import com.webnovel.domain.novel.entity.NovelEventLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NovelEventLogRepository extends JpaRepository<NovelEventLog, Long> {
}
