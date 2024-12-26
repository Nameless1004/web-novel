package com.webnovel.novel.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EpisodeCreateRequestDto {
    @NotBlank(message = "제목은 필수 입력값입니다")
    private String title;
    private String authorReview;
    @NotBlank(message = "내용은 필수 입력값입니다")
    private String content;
}
