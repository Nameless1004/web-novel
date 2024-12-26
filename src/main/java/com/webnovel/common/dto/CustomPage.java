package com.webnovel.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomPage<T> {
    private List<T> content;        // 현재 페이지의 데이터 리스트
    private int pageNumber;         // 현재 페이지 번호
    private int pageSize;           // 페이지 크기
    private long totalElements;     // 전체 데이터 개수
    private int totalPages;         // 전체 페이지 수
    private boolean isLast;         // 마지막 페이지 여부
    private boolean isFirst;        // 첫 페이지 여부
    private boolean hasNext;

    public CustomPage(List<T> content, Pageable pageable, long totalElements) {
        this.content = content;
        this.pageNumber = Math.max(pageable.getPageNumber(), 0);
        this.pageSize = Math.max(pageable.getPageSize(), 1);
        this.totalElements = totalElements;

        this.totalPages = (int) Math.ceil(totalElements / (double) Math.max(pageSize, 1));
        this.isFirst = this.pageNumber == 0;
        this.isLast = this.pageNumber >= (this.totalPages - 1);
        this.hasNext = !this.isLast;
    }
}
