package com.webnovel.comment.service;

import com.webnovel.comment.dto.CommentCreateDto;
import com.webnovel.comment.dto.CommentDetailsDto;
import com.webnovel.comment.dto.CommentUpdateDto;
import com.webnovel.comment.entity.Comment;
import com.webnovel.comment.repository.CommentRepository;
import com.webnovel.common.dto.ResponseDto;
import com.webnovel.common.exceptions.AccessDeniedException;
import com.webnovel.novel.entity.Episode;
import com.webnovel.novel.entity.Novel;
import com.webnovel.novel.repository.EpisodeRepository;
import com.webnovel.novel.repository.NovelRepository;
import com.webnovel.security.jwt.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final EpisodeRepository episodeRepository;
    private final CommentRepository commentRepository;
    private final NovelRepository novelRepository;

    @Override
    public ResponseDto<CommentDetailsDto> createComment(AuthUser authUser, long novelId, long episodeId, CommentCreateDto request) {
        String content = request.getContent();
        Episode episode = episodeRepository.findByIdOrElseThrow(episodeId);
        Comment comment = new Comment(episode, authUser.toUserEntity(), content);
        comment = commentRepository.save(comment);

        return ResponseDto.of(HttpStatus.CREATED,
                CommentDetailsDto.builder()
                    .authorUserName(authUser.getUsername())
                    .content(content)
                    .commentedAt(comment.getCommentedAt())
                    .build());
    }

    @Override
    public ResponseDto<CommentDetailsDto> updateComment(AuthUser authUser, long novelId, long episodeId, long commentId, CommentUpdateDto request) {
        String content = request.getContent();
        Comment comment = commentRepository.findByIdOrElseThrow(commentId);

        checkAuthority(authUser, comment);

        comment.update(request.getContent());

        return ResponseDto.of(HttpStatus.CREATED,
                CommentDetailsDto.builder()
                        .authorUserName(authUser.getUsername())
                        .content(content)
                        .commentedAt(comment.getCommentedAt())
                        .build());
    }

    @Override
    public ResponseDto<Void> deleteComment(AuthUser authUser, long novelId, long episodeId, long commentId) {
        Comment comment = commentRepository.findByIdOrElseThrow(commentId);
        checkAuthority(authUser, comment);

        commentRepository.delete(comment);
        return ResponseDto.of(HttpStatus.OK, "성공적으로 삭제됐습니다.");
    }

    @Override
    public ResponseDto<List<CommentDetailsDto>> getAllComments(long episodeId) {

        Episode episode = episodeRepository.findByIdOrElseThrow(episodeId);
        List<CommentDetailsDto> find = commentRepository.findAllByEpisode(episode)
                .stream()
                .map(x -> CommentDetailsDto.builder()
                        .commentedAt(x.getCommentedAt())
                        .content(x.getContent())
                        .authorUserName(x.getUser().getUsername())
                        .build())
                .toList();

        return ResponseDto.of(HttpStatus.OK, find);
    }

    private void checkAuthority(AuthUser authUser, Comment comment) {
        if(!comment.getUser().getUsername().equals(authUser.getUsername())) {
            throw new AccessDeniedException();
        }
    }
}
