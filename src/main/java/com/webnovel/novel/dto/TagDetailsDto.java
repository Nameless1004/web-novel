package com.webnovel.novel.dto;

import com.webnovel.novel.entity.Tag;
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
