package com.webnovel.domain.novel.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RecommendationCount {
    private long recommendationCount;

    public void increaseViewCount() {
        this.recommendationCount++;
    }
}