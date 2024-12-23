package com.webnovel.comment.service;

import com.webnovel.comment.dto.CommentCreateDto;
import com.webnovel.comment.dto.CommentDetailsDto;
import com.webnovel.comment.dto.CommentUpdateDto;
import com.webnovel.common.dto.ResponseDto;
import com.webnovel.security.jwt.AuthUser;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Override
    public ResponseDto<CommentDetailsDto> createComment(AuthUser authUser, long novelId, long episodeId, CommentCreateDto request) {
        return null;
    }

    @Override
    public ResponseDto<CommentDetailsDto> updateComment(AuthUser authUser, long novelId, long episodeId, long commentId, CommentUpdateDto request) {
        return null;
    }

    @Override
    public ResponseDto<Void> deleteComment(AuthUser authUser, long novelId, long episodeId, long commentId) {
        return null;
    }

    @Override
    public ResponseDto<List<CommentDetailsDto>> getAllComments(long novelId, long episodeId) {
        return null;
    }
}
