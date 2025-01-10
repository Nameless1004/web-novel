package com.webnovel.domain.novel.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class EpisodeUpdateDto {

    @NotBlank(message = "제목은 필수 입력값입니다")
    private String title;
    private String authorReview;
    @NotBlank(message = "내용은 필수 입력값입니다")
    private String content;
    private MultipartFile cover;
}
