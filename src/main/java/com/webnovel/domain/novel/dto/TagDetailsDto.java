package com.webnovel.domain.novel.dto;

import com.webnovel.domain.novel.entity.Tag;
import lombok.Data;

@Data
public class TagDetailsDto {

    private long id;
    private String name;

    public TagDetailsDto(Tag tag) {
        this.id = tag.getId();
        this.name = tag.getName();
    }
}
