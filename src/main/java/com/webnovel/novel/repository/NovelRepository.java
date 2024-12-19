package com.webnovel.novel.repository;

import com.webnovel.common.exceptions.NotFoundException;
import com.webnovel.novel.entity.Novel;
import io.lettuce.core.dynamic.annotation.Param;
import org.hibernate.annotations.NotFound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface NovelRepository extends JpaRepository<Novel, Long> {

    @Query("SELECT n FROM Novel n JOIN FETCH n.author WHERE n.id=:id")
    Optional<Novel> findNovelWithUserById(@Param("id") long id);
    default Novel findByNovelIdOrElseThrow(long novelId) {
        return findById(novelId)
                .orElseThrow(()-> new NotFoundException("Not found novel with id " + novelId));
    }
}
