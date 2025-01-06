package com.webnovel.domain.novel.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ViewCountDto {
    private long viewCount;

    public void increaseViewCount() {
        this.viewCount++;
    }
}
