package com.webnovel.comment.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDetailsDto {

    private String authorUserName;
    private LocalDateTime commentedAt;
    private String content;
}
