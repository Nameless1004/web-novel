package com.webnovel.domain.novel.repository;

import com.webnovel.common.exceptions.NotFoundException;
import com.webnovel.domain.novel.entity.Novel;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface NovelRepository extends JpaRepository<Novel, Long>, NovelCustomRepository {

    @Query("SELECT n FROM Novel n JOIN FETCH n.author WHERE n.id=:id")
    Optional<Novel> findNovelWithUserById(@Param("id") long id);

    default Novel findByNovelIdOrElseThrow(long novelId) {
        return findNovelWithUserById(novelId)
                .orElseThrow(()-> new NotFoundException("Not found novel with id " + novelId));
    }

    boolean existsByTitle(String title);

    Page<Novel> findAllByOrderByLastUpdatedAtDesc(Pageable pageable);
    Page<Novel> findAllByAuthorIdOrderByLastUpdatedAtDesc(long userId, Pageable pageable);
}
