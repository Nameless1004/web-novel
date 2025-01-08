package com.webnovel.domain.comment.controller;

import com.webnovel.domain.comment.dto.CommentCreateDto;
import com.webnovel.domain.comment.dto.CommentDetailsDto;
import com.webnovel.domain.comment.dto.CommentUpdateDto;
import com.webnovel.domain.comment.service.CommentService;
import com.webnovel.common.dto.ResponseDto;
import com.webnovel.domain.security.jwt.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/novels/{novelId}/episodes/{episodeId}/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ResponseDto<CommentDetailsDto>> createComment(
            @PathVariable Long novelId,
            @PathVariable Long episodeId,
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody CommentCreateDto createDto) {
        ResponseDto<CommentDetailsDto> result = commentService.createComment(authUser, novelId, episodeId, createDto);
        return result.toEntity();
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<ResponseDto<CommentDetailsDto>> updateComment(
            @PathVariable Long novelId,
            @PathVariable Long episodeId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody CommentUpdateDto updateDto) {
        ResponseDto<CommentDetailsDto> result = commentService.updateComment(authUser, novelId, episodeId, commentId, updateDto);
        return result.toEntity();
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ResponseDto<Void>> deleteComment(
            @PathVariable Long novelId,
            @PathVariable Long episodeId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal AuthUser authUser) {
        ResponseDto<Void> result = commentService.deleteComment(authUser, novelId, episodeId, commentId);
        return result.toEntity();
    }
    @GetMapping
    public ResponseEntity<ResponseDto<List<CommentDetailsDto>>> getComments(
            @PathVariable Long novelId,
            @PathVariable Long episodeId) {
        ResponseDto<List<CommentDetailsDto>> result = commentService.getAllComments(episodeId);
        return result.toEntity();
    }
}
