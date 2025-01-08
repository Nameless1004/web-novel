package com.webnovel.domain.novel.repository;

import com.webnovel.domain.novel.entity.EpisodeViewLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EpisodeViewLogRepository extends JpaRepository<EpisodeViewLog, Long> {
}
