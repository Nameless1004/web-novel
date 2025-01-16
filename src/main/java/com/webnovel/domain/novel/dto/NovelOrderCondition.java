package com.webnovel.domain.novel.dto;

import com.webnovel.common.exceptions.InvalidRequestException;

public enum NovelOrderCondition {
    NEW, VIEW, UPDATE;

    public static NovelOrderCondition parse(String condition){
        NovelOrderCondition[] values = values();
        for(NovelOrderCondition novelOrderCondition : values){
            if(novelOrderCondition.toString().equalsIgnoreCase(condition)){
                return novelOrderCondition;
            }
        }

        throw new InvalidRequestException("Invalid Order condition: " + condition);
    }
}
