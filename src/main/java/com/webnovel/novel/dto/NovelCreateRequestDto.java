package com.webnovel.novel.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class NovelCreateRequestDto {
    private Long authorId;
    private String genre;
    private String summary;
}
