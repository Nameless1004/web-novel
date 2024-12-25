package com.webnovel.novel.dto;

import com.webnovel.novel.enums.NovelStatus;
import lombok.Data;

import java.util.List;

@Data
public class NovelUpdateDto {
    private String title;
    private String summary;
    private NovelStatus status;
    private List<Long> tagIds;
}
