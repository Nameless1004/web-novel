package com.webnovel.comment.service;

import com.webnovel.comment.dto.CommentCreateDto;
import com.webnovel.comment.dto.CommentDetailsDto;
import com.webnovel.comment.dto.CommentUpdateDto;
import com.webnovel.common.dto.ResponseDto;
import com.webnovel.security.jwt.AuthUser;

import java.util.List;

public interface CommentService {

    ResponseDto<CommentDetailsDto> createComment(AuthUser authUser, long novelId, long episodeId, CommentCreateDto request);
    ResponseDto<CommentDetailsDto> updateComment(AuthUser authUser, long novelId, long episodeId, long commentId, CommentUpdateDto request);
    ResponseDto<Void> deleteComment(AuthUser authUser, long novelId, long episodeId, long commentId);
    ResponseDto<List<CommentDetailsDto>> getAllComments(long novelId, long episodeId);
}
