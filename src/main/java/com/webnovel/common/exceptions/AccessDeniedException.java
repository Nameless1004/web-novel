package com.webnovel.common.exceptions;

public class AccessDeniedException extends ServiceException{

    public AccessDeniedException() {
        super("권한이 없습니다.");
    }
}
