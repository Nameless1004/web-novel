package com.webnovel.novel.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class NovelCreateResponseDto {
    private Long novelId;
    private String authorUsername;
    private List<String> tag;
    private String summary;
}
