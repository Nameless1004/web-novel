package com.webnovel.novel.repository;

import com.webnovel.common.exceptions.NotFoundException;
import com.webnovel.novel.entity.Episode;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.Id;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface EpisodeRepository extends JpaRepository<Episode, Long>, EpisodeCustomRepository {
    int countEpisodeByNovelId(Long novelId);

    default Episode findByIdOrElseThrow(Long episodeId) {
        return findById(episodeId)
                .orElseThrow(() -> new NotFoundException("Episode not found"));
    }

    @Query("SELECT COALESCE(SUM(e.viewCount), 0) FROM Episode e WHERE e.novel.id = :novelId")
    long getTotalViewCount(@Param("novelId") Long novelId);

    @Query("SELECT COALESCE(SUM(e.recommendationCount),0) FROM Episode e WHERE e.novel.id = :novelId")
    long getTotalRecommendationCount(@Param("novelId") Long novelId);

    @Query("SELECT e.viewCount FROM Episode e WHERE e.id =:id")
    Optional<Long> findViewCountById(@Param("id") Long id);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT e FROM Episode e WHERE e.id=:id")
    Optional<Episode> findByIdWithOptimisticLock(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Episode e WHERE e.id=:id")
    Optional<Episode> findByIdWithPessimisticLock(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Episode e SET e.episodeNumber = e.episodeNumber - 1 " +
    "WHERE e.novel.id = :novelId AND e.episodeNumber > :deletedEpisodeNumber")
    int updateEpisodeNumbersAfterDeletion(@Param("novelId") long novelId, @Param("deletedEpisodeNumber") int deletedEpisodeNumber);
}
