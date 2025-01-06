package com.webnovel.domain.comment.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDetailsDto {

    private long id;
    private String authorUserName;
    private LocalDateTime commentedAt;
    private String content;
}
