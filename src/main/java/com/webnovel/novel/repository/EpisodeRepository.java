package com.webnovel.novel.repository;

import com.webnovel.novel.entity.Episode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {
    long countEpisodeByNovelId(Long novelId);
}
