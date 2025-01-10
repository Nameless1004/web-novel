package com.webnovel.domain.novel.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Accessors(chain = true)
public class NovelCreateRequestDto {
    @NotBlank(message = "제목은 필수 입력값입니다")
    private String title;
    private String synopsis;
    private List<Long> tagIds;
    private MultipartFile cover;
}
