package com.webnovel.novel.repository;

import com.webnovel.common.exceptions.NotFoundException;
import com.webnovel.novel.entity.Episode;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {
    int countEpisodeByNovelId(Long novelId);

    default Episode findByIdOrElseThrow(Long episodeId) {
        return findById(episodeId)
                .orElseThrow(() -> new NotFoundException("Episode not found"));
    }

    @Query("SELECT COALESCE(SUM(e.viewCount), 0) FROM Episode e WHERE e.novel.id = :novelId")
    long getTotalViewCount(@Param("novelId") Long novelId);

    @Query("SELECT COALESCE(SUM(e.recommendationCount),0) FROM Episode e WHERE e.novel.id = :novelId")
    long getTotalRecommendationCount(@Param("novelId") Long novelId);
}
