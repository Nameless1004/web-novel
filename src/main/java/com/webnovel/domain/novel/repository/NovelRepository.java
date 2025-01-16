package com.webnovel.domain.novel.repository;

import com.webnovel.common.exceptions.NotFoundException;
import com.webnovel.domain.novel.entity.Novel;
import com.webnovel.domain.security.jwt.AuthUser;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT n FROM Novel n  WHERE n.id=:id")
    Optional<Novel> findByIdWithPessimisticLock(Long id);
}
