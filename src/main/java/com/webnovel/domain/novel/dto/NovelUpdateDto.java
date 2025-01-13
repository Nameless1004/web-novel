package com.webnovel.domain.novel.dto;

import com.webnovel.domain.novel.enums.NovelStatus;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Accessors(chain = true)
public class NovelUpdateDto {
    private String title;
    private String synopsis;
    private NovelStatus status;
    private List<Long> tagIds;
    private MultipartFile cover;
}
