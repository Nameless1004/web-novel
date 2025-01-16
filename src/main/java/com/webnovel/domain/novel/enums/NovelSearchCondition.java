package com.webnovel.domain.novel.enums;

import com.webnovel.common.exceptions.InvalidRequestException;

public enum NovelSearchCondition {
    TITLE, AUTHOR, ALL;

    public static NovelSearchCondition parse(String condition) {
        NovelSearchCondition[] values = values();
        for (NovelSearchCondition value : values) {
            if(value.toString().equalsIgnoreCase(condition)) {
                return value;
            }
        }

        throw new InvalidRequestException("Invalid Search condition: " + condition);
    }
}
