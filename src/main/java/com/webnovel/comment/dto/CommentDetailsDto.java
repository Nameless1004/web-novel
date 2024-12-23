package com.webnovel.comment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDetailsDto {

    private String authorUserName;
    private LocalDateTime commentedAt;
    private String content;
}
