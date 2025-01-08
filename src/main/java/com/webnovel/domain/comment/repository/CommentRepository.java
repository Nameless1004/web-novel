package com.webnovel.domain.comment.repository;

import com.webnovel.domain.comment.entity.Comment;
import com.webnovel.common.exceptions.NotFoundException;
import com.webnovel.domain.novel.entity.Episode;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.id = :commentId")
    Optional<Comment> findWithUserById(@Param("commentId") long commentId);

    default Comment findByIdOrElseThrow(long commentId) {
        return findWithUserById(commentId).orElseThrow(() -> new NotFoundException("Comment not found"));
    }

    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.episode =:episode ORDER BY c.commentedAt DESC")
    List<Comment> findAllByEpisode(@Param("episode") Episode episode);

    boolean existsByUser_IdAndEpisode_Id(Long userId, Long episodeId);
}
