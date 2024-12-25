package com.webnovel.novel.dto;

import com.webnovel.novel.entity.Tag;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class NovelCreateRequestDto {
    private Long authorId;
    private String title;
    private String genre;
    private String summary;
    private List<Long> tagIds;
}
